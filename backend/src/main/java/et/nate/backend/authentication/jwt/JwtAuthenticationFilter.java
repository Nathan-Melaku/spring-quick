package et.nate.backend.authentication.jwt;

import com.nimbusds.jwt.JWTClaimNames;
import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.AuthUtils;
import et.nate.backend.config.SecurityConfigProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static java.util.Arrays.stream;

/**
 * Authentication filter that will validate and give permission for a valid JWT Holder.
 * It will use allowed endpoints from application configuration. and a jwt validation service
 * A valid token needs the following properties:
 * <ul>
 *     <li>The following claims need to exist in the jwt</li>
 *     <ul>
 *         <li>{@link JWTClaimNames.SUBJECT }</li>
 *         <li>{@link JWTClaimNames.ISSUED_AT}</li>
 *         <li>{@link AuthConstants.SCOPE}</li>
 *         <li>{@link AuthConstants.USER_CONTEXT_COOKIE}</li>
 *         <li>{@link JWTClaimNames.EXPIRATION_TIME}</li>
 *     </ul>
 *     <li>It should be signed by private key defined in {@link et.nate.backend.config.RSAKeyProperties}</li>
 *     <li>The hashed value of __Secure-UserContext cookie must match the one defined in the {@link AuthConstants.USER_CONTEXT_COOKIE} claim of jwt.
 *     @see <a href="https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html#token-sidejacking">OWASP cheatsheet for JWT</a>
 * </ul>
 */
@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * {@link SecurityConfigProperties} provides allowed endpoints since they shouldn't be processed in this filter.
     */
    private final SecurityConfigProperties securityConfigProperties;
    /**
     * {@link JwtValidationService} provides functions for validating a given jwt token.
     */
    private final JwtValidationService jwtValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        var uri = request.getRequestURI();
        var byPass = false;

        // Do not process allowed Endpoints
        for (String endpoint : securityConfigProperties.getAllowedEndpoints()) {
            if (endpoint.endsWith("/**")) {
                endpoint = endpoint.substring(0, endpoint.length() - 3);
                if (uri.startsWith(endpoint)) {
                    byPass = true;
                    break;
                }
            } else {
                if (uri.equals(endpoint)) {
                    byPass = true;
                    break;
                }
            }

        }
        if (byPass) {
            filterChain.doFilter(request, response);
        } else {
            // validate the token and authenticate.
            try {
                log.trace("Attempting to validate claims in Token");
                var token = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
                var claimSet = jwtValidationService.validate(token, new HashSet<>(Arrays.asList(
                        JWTClaimNames.SUBJECT,
                        JWTClaimNames.ISSUED_AT,
                        AuthConstants.SCOPE,
                        AuthConstants.USER_CONTEXT_COOKIE,
                        JWTClaimNames.EXPIRATION_TIME)));
                log.trace("Finished validating claims in Token");

                log.trace("Attempting to validate user context cookie in Token");
                var contextFromJWT = (String) claimSet.getClaim(AuthConstants.USER_CONTEXT_COOKIE);
                var contextCookie = Arrays.stream(request.getCookies())
                        .filter(c -> c.getName().equals(AuthConstants.USER_CONTEXT_COOKIE))
                        .map(Cookie::getValue)
                        .findFirst();

                if (contextCookie.isEmpty() || AuthUtils.dontMatchContextHash(contextFromJWT, contextCookie.get())) {
                    log.error("Invalid user context cookie");
                    throw new CustomJwtValidationException(AuthConstants.INVALID_TOKEN_ERROR);
                }
                log.trace("Finished validating user context cookie in Token");

                var privileges = new ArrayList<GrantedAuthority>();
                var scopeRole = claimSet.getClaim(AuthConstants.SCOPE).toString()
                        .replace("[", "")
                        .replace("]", "")
                        .split(" ");
                stream(scopeRole).forEach(scope ->
                        privileges.add(new SimpleGrantedAuthority(scope))
                );
                var authentication = new UsernamePasswordAuthenticationToken(claimSet.getSubject(), null, privileges);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.trace("Finished Authenticating the user");
            } catch (CustomJwtValidationException e) {
                log.error("Invalid claims in Token", e);
                request.setAttribute(AuthConstants.ERROR, e.getMessage());
            }

            filterChain.doFilter(request, response);
        }
    }
}
