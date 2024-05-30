package et.nate.backend.authentication.registration;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.jwt.JwtMintingService;
import et.nate.backend.authentication.registration.dto.RegistrationRequest;
import et.nate.backend.authentication.registration.dto.RegistrationResponse;
import et.nate.backend.authentication.registration.dto.VerificationResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static et.nate.backend.authentication.AuthUtils.setCookies;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final RegistrationVerificationService verificationService;
    private final JwtMintingService jwtMintingService;

    @PostMapping("/auth/register")
    public RegistrationResponse register(@RequestBody @Valid RegistrationRequest registration, HttpServletResponse response) throws UserAlreadyExistsException {

        var id = registrationService.register(registration);
        var tokens = jwtMintingService.generateAccessToken(new UsernamePasswordAuthenticationToken(registration.email(), registration.password()), id);
        setCookies(response, tokens);
        return new RegistrationResponse(tokens.accessToken(), tokens.refreshToken());
    }

    /**
     * this is an authenticated Endpoint. Only a signed-in user can verify registration.
     * @param token: verification Token to be verified
     * @param authentication: authentication object of the user
     * @return {@link RegistrationResponse}
     * @throws RegistrationVerificationException when verification failed
     */
    @PostMapping("/verify")
    public VerificationResponse verify(@RequestParam String token, Authentication authentication) throws RegistrationVerificationException {

        // verify token
        verificationService.verifyToken(token, authentication);

        return new VerificationResponse(AuthConstants.VERIFICATION_COMPLETED);
    }

    @PostMapping("/resend")
    public VerificationResponse resend(Authentication authentication) throws RegistrationVerificationException {
        verificationService.resendToken(authentication);
        return new VerificationResponse(AuthConstants.RESEND_COMPLETED);
    }
}
