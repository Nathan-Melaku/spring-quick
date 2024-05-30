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

/**
 * This is a service that is responsible for creating new tokens.
 * It has methods for creating new access and refresh tokens.
 */
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

    /**
     * method to generate access Tokens. Access tokens can be generated from two kinds of authentication.
     * <ul>
     *     <li><b>oauth:</b> when user authenticates with social media.</li>
     *     <li><b>form:</b> when user authenticates with login or registration from</li>
     * </ul>
     * @param authentication could be of type {@link OAuth2AuthenticationToken} or {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
     * @param id database id of the user. when logging in through social media, this will be 0, and it will be overridden in this method.
     * @return {@link TokenResult}
     */
    public TokenResult generateAccessToken(Authentication authentication, long id) {
        log.trace("Generating access token for user: {}", authentication.getName());
        User user;
        if (Objects.requireNonNull(authentication) instanceof OAuth2AuthenticationToken) {
            log.trace("Detected OAuth2AuthenticationToken, User logged in through Social Media");
            var token = (OAuth2AuthenticationToken) authentication;
            var oAuth2User = (OAuth2User) token.getPrincipal();
            var registrationId = token.getAuthorizedClientRegistrationId();
            user = SocialLoginExtractor.extractUser(oAuth2User, extractors, registrationId);
            log.trace("Searching for user in Database");
            var userDb = userRepository.findByEmail(user.getEmail());
            assert userDb.isPresent();
            log.trace("Found user in Database");
            id = userDb.get().getId();
        }

        var userFingerprint = getRandom();
        assert userFingerprint != null;
        log.trace("Calculated Fingerprint: {}", userFingerprint);
        var accessToken = mintAccessToken(id, userFingerprint.hash);
        var refreshToken = mintRefreshToken(id, null, userFingerprint.hash);
        log.trace("Finished generating access token");
        return new TokenResult(accessToken, refreshToken, userFingerprint.value, accessTokenExpirationSeconds, refreshTokenExpirationMinutes * 60);
    }


    public TokenResult generateRefreshToken(Long id, Instant expiresAt) {
        log.trace("Generating refresh token for user: {}", id);
        var userFingerprint = getRandom();
        assert userFingerprint != null;
        var refreshToken = mintRefreshToken(id, expiresAt, userFingerprint.hash);
        var accessToken = mintAccessToken(id, userFingerprint.hash);
        log.trace("Finished generating refresh token");
        return new TokenResult(accessToken, refreshToken, userFingerprint.value, accessTokenExpirationSeconds, refreshTokenExpirationMinutes * 60);
    }

    /**
     * Helper method for generating Access Token. Access Token requires a ROLE in the SCOPE claim, so we get that from DB
     * @param id of user in DB.
     * @param userFingerprint user context fingerprint that should be saved in jwt and cookie.
     * @return string representation of the access Token.
     */
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

    /**
     * helper method for generating Refresh token.
     * @param id of user in DB.
     * @param expiresAt expiration date of the new token. Expiration date of new refresh token must not exceed the old one.
     *                  so if the token is being minted for the first time, we expect a null value for this param.
     *                  then token expiration will be set according to application.yaml file. If we are generating a new value for rotation,
     *                  then since we already have a refresh token, the new token must have the same expiration date as the old one.
     * @param userFingerprint user context fingerprint that should be saved in jwt and cookie.
     * @return string representation of refresh token.
     */
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

    /**
     * Helper method to generate a random value and a hash of this random value
     * @return {@link Fingerprint} that contains a rando value and a has of this value.
     * Since we provide a valid hashing algorithm {@link NoSuchAlgorithmException} should never be encountered.
     */
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
