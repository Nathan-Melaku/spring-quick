package et.nate.backend.authentication.jwt;

import et.nate.backend.authentication.dto.TokenDTO;
import et.nate.backend.authentication.exceptions.CustomJwtValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class JwtController {

    private final JwtMintingService jwtMintingService;
    private final JwtValidationService jwtValidationService;

    @PostMapping("/token/refresh")
    public TokenDTO refreshToken(@RequestHeader(name = "Authorization") String refreshToken) throws CustomJwtValidationException {
        assert refreshToken != null;
        // check for token validity and
        var claims = jwtValidationService.validateRefreshToken(refreshToken);
        // mint a new access and refresh token and add refresh token to database.
        var tokens = jwtMintingService.generateRefreshToken(claims.getSubject());
        return new TokenDTO(tokens.accessToken(), tokens.refreshToken());
    }


}
