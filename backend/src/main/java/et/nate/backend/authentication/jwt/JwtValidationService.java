package et.nate.backend.authentication.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import et.nate.backend.authentication.exceptions.CustomJwtValidationException;
import et.nate.backend.authentication.model.RefreshToken;
import et.nate.backend.authentication.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class JwtValidationService {

    private final JWKSource<SecurityContext> jwkSource;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JWTClaimsSet validate(String token, HashSet<String> claimsToValidate) throws CustomJwtValidationException {
        JWTClaimsSet claimsSet = null;
        if (token != null && token.startsWith("Bearer ")) {
            var accessToken = token.substring(7);
            var jwtProcessor = new DefaultJWTProcessor<>();
            var keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, jwkSource);
            jwtProcessor.setJWSKeySelector(keySelector);
            jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                    new JWTClaimsSet.Builder().issuer("self").build(), claimsToValidate));
            try {
                claimsSet = jwtProcessor.process(accessToken, null);
            } catch (Exception e) {
                throw new CustomJwtValidationException("Failed to validate JWT token", e);
            }
        }

        if (claimsSet == null) {
            throw new CustomJwtValidationException("Invalid token");
        }

        return claimsSet;
    }

    public JWTClaimsSet validateRefreshToken(String token) throws CustomJwtValidationException {

        var claimsSet = validate(token, new HashSet<>(Arrays.asList(
                JWTClaimNames.SUBJECT,
                JWTClaimNames.ISSUED_AT,
                JWTClaimNames.EXPIRATION_TIME)));

        // validate against the database.
        var refreshToken = token.substring(7);
        //var hashed = bCryptPasswordEncoder.encode(refreshToken);
        var result = refreshTokenRepository.findRefreshTokenByToken(refreshToken);

        if (!result.isEmpty()) {
            throw new CustomJwtValidationException("Refresh token reused");
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
