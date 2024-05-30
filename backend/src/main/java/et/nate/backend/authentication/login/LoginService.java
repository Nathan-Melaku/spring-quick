package et.nate.backend.authentication.login;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.jwt.JwtMintingService;
import et.nate.backend.authentication.jwt.TokenResult;
import et.nate.backend.data.model.ForgotPasswordToken;
import et.nate.backend.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    @Value("${app.security.password-reset-expiration-hours}")
    private int passwordResetExpirationHours;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtMintingService jwtMintingService;
    private final ApplicationEventPublisher eventPublisher;

    public TokenResult login(String email, String password) {
        var user = userRepository.findByEmail(email);

        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new BadCredentialsException(AuthConstants.BAD_CREDENTIALS);
        }

        var tokens = jwtMintingService.generateAccessToken(new UsernamePasswordAuthenticationToken(email, password), user.get().getId());

        return new TokenResult(tokens.accessToken(), tokens.refreshToken(), tokens.userContextCookie(), tokens.accessExpiresAt(), tokens.refreshExpiresAt());
    }


    public void sendLoginLink(String email) throws BadCredentialsException {

        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new BadCredentialsException(AuthConstants.BAD_CREDENTIALS);
        }

        var userDb = user.get();
        var forgotPasswordToken = new ForgotPasswordToken(0,
                UUID.randomUUID().toString(),
                Instant.now().plus(passwordResetExpirationHours, ChronoUnit.HOURS));
        userDb.setForgotPasswordToken(forgotPasswordToken);
        userRepository.save(userDb);

        eventPublisher.publishEvent(new OnForgotPasswordEvent(email, forgotPasswordToken.getToken(), userDb.getId()));
    }

    public TokenResult loginForReset(String token, Long id) {
        var user = userRepository.findById(id);
        if (user.isEmpty() ) {
            throw new BadCredentialsException(AuthConstants.BAD_CREDENTIALS);
        }
        var userDb = user.get();
        var forgotToken = userDb.getForgotPasswordToken();
        if (!forgotToken.getToken().equals(token) || forgotToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadCredentialsException(AuthConstants.BAD_CREDENTIALS);
        }

        userDb.setForgotPasswordToken(null);
        userRepository.save(userDb);

        var tokens = jwtMintingService.generateAccessToken(new UsernamePasswordAuthenticationToken("" , ""), id);

        return new TokenResult(tokens.accessToken(), tokens.refreshToken(), tokens.userContextCookie(), tokens.accessExpiresAt(), tokens.refreshExpiresAt());
    }
}
