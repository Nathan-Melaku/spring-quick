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
import et.nate.backend.data.model.RefreshToken;
import et.nate.backend.data.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class JwtValidationService {

    private final JWKSource<SecurityContext> jwkSource;
    private final RefreshTokenRepository refreshTokenRepository;

    public JWTClaimsSet validate(String token, HashSet<String> claimsToValidate) throws CustomJwtValidationException {
        JWTClaimsSet claimsSet = null;
        if (token != null && token.startsWith(AuthConstants.BEARER)) {
            var accessToken = token.substring(7);
            var jwtProcessor = new DefaultJWTProcessor<>();
            var keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
            jwtProcessor.setJWSKeySelector(keySelector);
            jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                    new JWTClaimsSet.Builder().issuer(AuthConstants.ISSUER).build(), claimsToValidate));
            try {
                claimsSet = jwtProcessor.process(accessToken, null);
            } catch (Exception e) {
                throw new CustomJwtValidationException(AuthConstants.TOKEN_VALIDATION_ERROR, e);
            }
        }

        if (claimsSet == null) {
            throw new CustomJwtValidationException(AuthConstants.INVALID_TOKEN_ERROR);
        }

        return claimsSet;
    }

    public JWTClaimsSet validateRefreshToken(String token) throws CustomJwtValidationException {

        var claimsSet = validate(token, new HashSet<>(Arrays.asList(
                JWTClaimNames.SUBJECT,
                JWTClaimNames.ISSUED_AT,
                JWTClaimNames.EXPIRATION_TIME)));

        // validate against the database.
        // TODO try hashing the access token
        var refreshToken = token.substring(7);
        var result = refreshTokenRepository.findRefreshTokenByToken(refreshToken);

        if (!result.isEmpty()) {
            throw new CustomJwtValidationException(AuthConstants.REFRESH_TOKEN_REUSED_ERROR);
        }

        refreshTokenRepository.save(new RefreshToken(
                0,
                refreshToken,
                claimsSet.getIssueTime().toInstant(),
                claimsSet.getExpirationTime().toInstant()
        ));

        return claimsSet;
    }
}
