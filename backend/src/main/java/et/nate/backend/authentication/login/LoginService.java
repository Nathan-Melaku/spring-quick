package et.nate.backend.authentication.login;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.jwt.JwtMintingService;
import et.nate.backend.authentication.login.dto.LoginResponseDto;
import et.nate.backend.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtMintingService jwtMintingService;

    public LoginResponseDto login(String email, String password) {
        var user = userRepository.findByEmail(email);

        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            throw new BadCredentialsException(AuthConstants.BAD_CREDENTIALS);
        }

        var tokens = jwtMintingService.generateAccessToken(new UsernamePasswordAuthenticationToken(email, password));

        return new LoginResponseDto(tokens.accessToken(), tokens.refreshToken());
    }
}
