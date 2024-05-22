package et.nate.backend.authentication.jwt;

import com.nimbusds.jwt.JWTClaimNames;
import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.config.SecurityConfigProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityConfigProperties securityConfigProperties;
    private final JwtValidationService jwtValidationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var uri = request.getRequestURI();
        var byPass = false;

        // Do not process allowed Endpoints
        for (String endpoint : securityConfigProperties.getAllowedEndpoints()) {
            if (endpoint.startsWith(uri)) {
                byPass = true;
                break;
            }
        }
        if (byPass) {
            filterChain.doFilter(request, response);
        } else {
            // validate the token and authenticate.
            try {
                var token = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
                var claimSet = jwtValidationService.validate(token, new HashSet<>(Arrays.asList(
                        JWTClaimNames.SUBJECT,
                        JWTClaimNames.ISSUED_AT,
                        AuthConstants.SCOPE,
                        JWTClaimNames.EXPIRATION_TIME)));

                var privileges = new ArrayList<GrantedAuthority>();
                stream(claimSet.getClaim(AuthConstants.SCOPE).toString().split(" ")).forEach(scope ->
                        privileges.add(new SimpleGrantedAuthority(scope))
                );
                var authentication = new UsernamePasswordAuthenticationToken(claimSet.getSubject(), null, privileges);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (CustomJwtValidationException e) {
                // send an error message
                request.setAttribute(AuthConstants.ERROR, e.getMessage());
            }

            filterChain.doFilter(request, response);
        }
    }
}
