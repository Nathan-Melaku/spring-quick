package et.nate.backend.authentication.jwt;

import et.nate.backend.authentication.AuthConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
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

    @Operation(summary = "request for a token refresh",
            description = "creates new tokens and sets new cookies that match them." +
                    "refreshToken then will be added to denyList in db for rotation.",
            security = {@SecurityRequirement(name = "Unauthenticated")})
    @PostMapping("/token/refresh")
    public TokenDTO refreshToken(@NotNull @RequestHeader(name = AuthConstants.AUTHORIZATION_HEADER) String refreshToken,
                                 HttpServletRequest request, HttpServletResponse response)
            throws CustomJwtValidationException {

        var claims = jwtValidationService.validateRefreshToken(refreshToken, request);
        var tokens = jwtMintingService.generateRefreshToken(Long.parseLong(claims.getSubject()), claims.getExpirationTime().toInstant());
        setCookies(response, tokens);
        return new TokenDTO(tokens.accessToken(), tokens.refreshToken());
    }
}
