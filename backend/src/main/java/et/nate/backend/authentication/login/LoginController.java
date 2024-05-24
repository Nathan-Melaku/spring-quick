package et.nate.backend.authentication.login;

import et.nate.backend.authentication.login.dto.LoginRequestDto;
import et.nate.backend.authentication.login.dto.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginData) {
        var tokens =
                loginService.login(loginData.email(), loginData.password());
        return new LoginResponseDto(tokens.access_token(), tokens.refresh_token());
    }
}
