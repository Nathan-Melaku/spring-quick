package et.nate.backend.authentication.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.AuthUtils;
import et.nate.backend.data.model.RefreshToken;
import et.nate.backend.data.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Class Provides two methods. one used for a general validation of JWT. other for validating Refresh token.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtValidationService {

    private final JWKSource<SecurityContext> jwkSource;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * A general Token validation method, which will only validate provided Claims. User context cookie check is done
     * at the call site.
     * @param token to be validated
     * @param claimsToValidate claims that should exist in the token.
     * @return the {@link JWTClaimsSet} that are found in the token.
     * @throws CustomJwtValidationException if the token is invalid, or couldn't be properly validated.
     * TODO refactor so that access token validation is done in this class and make this method private.
     */
    public JWTClaimsSet validate(String token, HashSet<String> claimsToValidate) throws CustomJwtValidationException {

        if (token == null || !token.startsWith(AuthConstants.BEARER)) {
            throw new CustomJwtValidationException(AuthConstants.INVALID_TOKEN_ERROR);
        }

        token = token.substring(7);
        var jwtProcessor = new DefaultJWTProcessor<>();
        var keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
        jwtProcessor.setJWSKeySelector(keySelector);
        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new JWTClaimsSet.Builder().issuer(AuthConstants.ISSUER).build(), claimsToValidate));
        try {
            return jwtProcessor.process(token, null);
        } catch (Exception e) {
            throw new CustomJwtValidationException(e.getMessage(), AuthConstants.TOKEN_VALIDATION_ERROR);
        }
    }

    /**
     * Method to validate RefreshToken.
     * @param token token to be validated
     * @param request to get the cookies from. since we have to check for __Secure-UserContext cookie to have the same value as in the jwt
     * @return the {@link JWTClaimsSet} that are found in the token.
     * @throws CustomJwtValidationException if the token is invalid, or couldn't be properly validated.
     */
    public JWTClaimsSet validateRefreshToken(String token, HttpServletRequest request) throws CustomJwtValidationException {
        log.trace("Validating refresh token");
        var claimsSet = validate(token, new HashSet<>(Arrays.asList(
                JWTClaimNames.SUBJECT,
                JWTClaimNames.ISSUED_AT,
                JWTClaimNames.EXPIRATION_TIME,
                AuthConstants.USER_CONTEXT_REFRESH_COOKIE)));

        // take the last 70 chars for bCrypt to work properly.
        var refreshToken = token.substring(token.length() - 70);
        var user = userRepository.findById(Long.parseLong(claimsSet.getSubject()));

        var contextFromJWT = (String) claimsSet.getClaim(AuthConstants.USER_CONTEXT_REFRESH_COOKIE);
        var contextCookie = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(AuthConstants.USER_CONTEXT_REFRESH_COOKIE))
                .map(Cookie::getValue)
                .findFirst();

        if (user.isEmpty() || contextCookie.isEmpty() || AuthUtils.dontMatchContextHash(contextFromJWT, contextCookie.get())) {
            log.error("Refresh token doesn't belong to a valid user");
            throw new CustomJwtValidationException(AuthConstants.INVALID_TOKEN_ERROR);
        }
        log.trace("Token belongs to a valid user");
        var refreshTokens = user.get().getRefreshTokens();

        var result = refreshTokens.stream()
                .anyMatch(t -> passwordEncoder.matches(refreshToken, t.getToken()));

        if (result) {
            log.error("Refresh token already used.");
            throw new CustomJwtValidationException(AuthConstants.REFRESH_TOKEN_REUSED_ERROR);
        }
        log.trace("Detected a fresh Refresh token.");
        var userDb = user.get();
        refreshTokens.add(new RefreshToken(
                0,
                passwordEncoder.encode(refreshToken),
                claimsSet.getIssueTime().toInstant(),
                claimsSet.getExpirationTime().toInstant()
        ));

        userDb.setRefreshTokens(refreshTokens);
        userRepository.save(userDb);
        log.trace("Refresh token validated and added to Deny list for later use.");
        return claimsSet;
    }
}
