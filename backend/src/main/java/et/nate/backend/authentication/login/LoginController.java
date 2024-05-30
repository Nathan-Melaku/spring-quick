package et.nate.backend.authentication.login;

import et.nate.backend.authentication.login.dto.LoginRequestDto;
import et.nate.backend.authentication.login.dto.LoginResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static et.nate.backend.authentication.AuthUtils.setCookies;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginData, HttpServletResponse response) {
        var tokens = loginService.login(loginData.email(), loginData.password());
        setCookies(response, tokens);

        return new LoginResponseDto(tokens.accessToken(), tokens.refreshToken());
    }
}
