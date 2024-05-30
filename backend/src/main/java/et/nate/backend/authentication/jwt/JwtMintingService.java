package et.nate.backend.authentication.jwt;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.oauth.SocialLoginExtractor;
import et.nate.backend.authentication.oauth.UserInfoExtractor;
import et.nate.backend.data.model.User;
import et.nate.backend.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtMintingService {

    private final JwtEncoder jwtEncoder;
    private final List<UserInfoExtractor> extractors;
    private final UserRepository userRepository;

    @Value("${app.security.access-token-expiration-seconds}")
    private int accessTokenExpirationSeconds;

    @Value("${app.security.refresh-token-expiration-minutes}")
    private int refreshTokenExpirationMinutes;

    public TokenResult generateAccessToken(Authentication authentication, long id) {
        User user;
        if (Objects.requireNonNull(authentication) instanceof OAuth2AuthenticationToken) {
            var token = (OAuth2AuthenticationToken) authentication;
            var oAuth2User = (OAuth2User) token.getPrincipal();
            var registrationId = token.getAuthorizedClientRegistrationId();
            user = SocialLoginExtractor.extractUser(oAuth2User, extractors, registrationId);

            var userDb = userRepository.findByEmail(user.getEmail());
            assert userDb.isPresent();
            id = userDb.get().getId();
        }

        var userFingerprint = getRandom();
        assert userFingerprint != null;
        var accessToken = mintAccessToken(id, userFingerprint.hash);
        var refreshToken = mintRefreshToken(id, null, userFingerprint.hash);
        return new TokenResult(accessToken, refreshToken, userFingerprint.value, accessTokenExpirationSeconds, refreshTokenExpirationMinutes * 60);
    }


    public TokenResult generateRefreshToken(Long id, Instant expiresAt) {
        var userFingerprint = getRandom();
        assert userFingerprint != null;
        var refreshToken = mintRefreshToken(id, expiresAt, userFingerprint.hash);
        var accessToken = mintAccessToken(id, userFingerprint.hash);
        return new TokenResult(accessToken, refreshToken, userFingerprint.value, accessTokenExpirationSeconds, refreshTokenExpirationMinutes * 60);
    }

    private String mintAccessToken(Long id, String userFingerprint) {
        var now = Instant.now();
        var scope = new ArrayList<String>();
        userRepository.findById(id).ifPresent(user ->
                user.getRoles().forEach(role ->
                        scope.add(role.getName()))
        );
        var claims = JwtClaimsSet.builder()
                .issuer(AuthConstants.ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenExpirationSeconds, ChronoUnit.SECONDS))
                .subject(Long.toString(id))
                .claim(AuthConstants.SCOPE, scope)
                .claim(AuthConstants.USER_CONTEXT_COOKIE, userFingerprint)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    // expiration date of new refresh token must not exceed the old one.
    private String mintRefreshToken(Long id, Instant expiresAt, String userFingerprint) {
        var now = Instant.now();
        Instant refreshTokenExpiresAt;
        if (expiresAt != null) {
            refreshTokenExpiresAt = expiresAt;
        } else {
            refreshTokenExpiresAt = now.plus(refreshTokenExpirationMinutes, ChronoUnit.MINUTES);
        }
        var claims = JwtClaimsSet.builder()
                .issuer(AuthConstants.ISSUER)
                .issuedAt(Instant.now())
                .expiresAt(refreshTokenExpiresAt)
                .subject(Long.toString(id))
                .claim(AuthConstants.USER_CONTEXT_REFRESH_COOKIE, userFingerprint)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private Fingerprint getRandom() {
        var secureRandom = new SecureRandom();
        byte[] randomFgp = new byte[50];
        secureRandom.nextBytes(randomFgp);
        var hexFormat = HexFormat.of();
        var value = hexFormat.formatHex(randomFgp);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] userFingerprintDigest = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            String userFingerprintHash = hexFormat.formatHex(userFingerprintDigest);
            return new Fingerprint(value, userFingerprintHash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private record Fingerprint(String value, String hash) {
    }
}
