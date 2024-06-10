package et.nate.backend.authentication.login;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.login.dto.ForgotPasswordRequestDto;
import et.nate.backend.authentication.login.dto.ForgotPasswordResponseDto;
import et.nate.backend.authentication.login.dto.LoginRequestDto;
import et.nate.backend.authentication.login.dto.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static et.nate.backend.authentication.AuthUtils.setCookies;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "request for login",
            description = "if the provided credentials are correct, it creates new tokens and sets new cookies that match them.",
            security = {@SecurityRequirement(name = "Unauthenticated")})
    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto loginData, HttpServletResponse response) {
        var tokens = loginService.login(loginData.email(), loginData.password());
        setCookies(response, tokens);

        return new LoginResponseDto(tokens.accessToken(), tokens.refreshToken());
    }

    @Operation(summary = "request for a password reset in case of a forgotten password.",
            description = "generates a magic login url and emails it to the user.",
            security = {@SecurityRequirement(name = "Unauthenticated")}
    )
    @PostMapping("/forgot")
    public ForgotPasswordResponseDto forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        loginService.sendLoginLink(request.email());

        return new ForgotPasswordResponseDto(AuthConstants.LOGIN_LINK_SENT);
    }

    @Operation(summary = "endpoint to validate a magic login.",
            description = "if the id and forgot password link are correct generates a new set of tokens.",
            security = {@SecurityRequirement(name = "Unauthenticated")}
    )
    @GetMapping("/reset")
    public LoginResponseDto reset(@RequestParam String token, @RequestParam Long id, HttpServletResponse response) {
        var tokens = loginService.loginForReset(token, id);
        setCookies(response, tokens);
        return new LoginResponseDto(tokens.accessToken(), tokens.refreshToken());
    }
}
