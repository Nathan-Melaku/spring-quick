package et.nate.backend.authentication.jwt;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.oauth.SocialLoginExtractor;
import et.nate.backend.data.model.User;
import et.nate.backend.authentication.oauth.UserInfoExtractor;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    private Instant refreshTokenExpiresAt;
    private Instant refreshTokenCreatedAt;

    public TokenResult generateAccessToken(Authentication authentication) {
        User user;
        if (Objects.requireNonNull(authentication) instanceof OAuth2AuthenticationToken) {
            var token = (OAuth2AuthenticationToken) authentication;
            var oAuth2User = (OAuth2User) token.getPrincipal();
            var registrationId = token.getAuthorizedClientRegistrationId();
            user = SocialLoginExtractor.extractUser(oAuth2User, extractors, registrationId);
        } else {
            // TODO Alternate authentication FORM or BASIC
            user = User.builder("email").build();
        }

        var accessToken = mintAccessToken(user.getEmail());
        var refreshToken = mintRefreshToken(user.getEmail());
        return new TokenResult(accessToken, refreshToken, refreshTokenCreatedAt, refreshTokenExpiresAt);
    }


    public TokenDTO generateRefreshToken(String email) {
        var refreshToken = mintRefreshToken(email);
        var accessToken = mintAccessToken(email);
        return new TokenDTO(accessToken, refreshToken);
    }

    private String mintAccessToken(String subject) {
        var now = Instant.now();
        var scope = new ArrayList<String>();
        userRepository.findByEmail(subject).ifPresent(user ->
                user.getRoles().forEach(role ->
                        scope.add(role.getName()))
        );
        var claims = JwtClaimsSet.builder()
                .issuer(AuthConstants.ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenExpirationSeconds, ChronoUnit.SECONDS))
                .subject(subject)
                .claim(AuthConstants.SCOPE, scope)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String mintRefreshToken(String email) {
        var now = Instant.now();
        refreshTokenExpiresAt = now.plus(refreshTokenExpirationMinutes, ChronoUnit.MINUTES);
        refreshTokenCreatedAt = now;
        var claims = JwtClaimsSet.builder()
                .issuer(AuthConstants.ISSUER)
                .issuedAt(Instant.now())
                .expiresAt(refreshTokenExpiresAt)
                .subject(email)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
