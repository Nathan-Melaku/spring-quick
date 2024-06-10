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

/**
 * Service class that implements all login related tasks.
 */
@Service
@RequiredArgsConstructor
public class LoginService {

    @Value("${app.security.password-reset-expiration-hours}")
    private int passwordResetExpirationHours;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtMintingService jwtMintingService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Authenticates through email and password. and generates tokens if the credentials are correct.
     * @param email of the user to be authenticated.
     * @param password of the user to be authenticated.
     * @return {@link TokenResult} that has all the info needed to send an authenticated response.
     * @throws BadCredentialsException if the provided credentials doesn't match the user in db.
     */
    public TokenResult login(String email, String password) throws BadCredentialsException {
        var user = userRepository.findByEmail(email);

        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new BadCredentialsException(AuthConstants.BAD_CREDENTIALS);
        }

        var tokens = jwtMintingService.generateAccessToken(new UsernamePasswordAuthenticationToken(email, password), user.get().getId());

        return new TokenResult(tokens.accessToken(), tokens.refreshToken(), tokens.userContextCookie(), tokens.accessExpiresAt(), tokens.refreshExpiresAt());
    }

    /**
     * Generates a random link and sends it to the provided email.
     * @param email to send the link to
     * @throws BadCredentialsException if a user with the provided email doesn't exist.
     */
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

    /**
     * Authenticates a reset link by checking the provided token in the link exists for the user with the provided id.
     * @param token forgot password token in the provided link
     * @param id id of the user to authenticate.
     * @return {@link TokenResult} that has all the info needed to send an authenticated response.
     * @throws BadCredentialsException if the provided token doesn't exist for the user with the id, or if the user doesn't exist.
     */
    public TokenResult loginForReset(String token, Long id) throws BadCredentialsException {
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
