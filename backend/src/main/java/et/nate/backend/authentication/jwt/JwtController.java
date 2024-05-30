package et.nate.backend.authentication.jwt;

import et.nate.backend.authentication.AuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static et.nate.backend.authentication.AuthUtils.setCookies;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class JwtController {

    private final JwtMintingService jwtMintingService;
    private final JwtValidationService jwtValidationService;

    @PostMapping("/token/refresh")
    public TokenDTO refreshToken(@RequestHeader(name = AuthConstants.AUTHORIZATION_HEADER) String refreshToken,
                                 HttpServletRequest request, HttpServletResponse response)
            throws CustomJwtValidationException {
        assert refreshToken != null;

        // check for token validity and
        var claims = jwtValidationService.validateRefreshToken(refreshToken, request);
        // mint a new access and refresh token and add refresh token to database.
        var tokens = jwtMintingService.generateRefreshToken(claims.getSubject(), claims.getExpirationTime().toInstant());
        setCookies(response, tokens);
        return new TokenDTO(tokens.accessToken(), tokens.refreshToken());
    }
}
