package et.nate.backend.authentication.login;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.login.dto.ForgotPasswordRequestDto;
import et.nate.backend.authentication.login.dto.ForgotPasswordResponseDto;
import et.nate.backend.authentication.login.dto.LoginRequestDto;
import et.nate.backend.authentication.login.dto.LoginResponseDto;
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

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto loginData, HttpServletResponse response) {
        var tokens = loginService.login(loginData.email(), loginData.password());
        setCookies(response, tokens);

        return new LoginResponseDto(tokens.accessToken(), tokens.refreshToken());
    }

    @PostMapping("/forgot")
    public ForgotPasswordResponseDto forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        loginService.sendLoginLink(request.email());

        return new ForgotPasswordResponseDto(AuthConstants.LOGIN_LINK_SENT);
    }

    @GetMapping("/reset")
    public LoginResponseDto reset(@RequestParam String token, @RequestParam Long id, HttpServletResponse response) {
        var tokens = loginService.loginForReset(token, id);
        setCookies(response, tokens);
        return new LoginResponseDto(tokens.accessToken(), tokens.refreshToken());
    }
}
