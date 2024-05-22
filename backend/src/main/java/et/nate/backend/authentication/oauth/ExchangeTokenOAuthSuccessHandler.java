package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.jwt.JwtMintingService;
import et.nate.backend.authentication.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeTokenOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth.redirect-uri}")
    private String redirectUri;

    private final JwtMintingService jwtMintingService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain,
                                        Authentication authentication) throws IOException, ServletException {
        handle(request, response, authentication);
        super.clearAuthenticationAttributes(request);
    }

    @Override
    protected void handle(HttpServletRequest request,
                          HttpServletResponse response,
                          Authentication authentication) throws IOException, ServletException {
        var target = redirectUri.isBlank() ? determineTargetUrl(request, response) : redirectUri;
        var token = jwtMintingService.generateAccessToken(authentication);
        target = UriComponentsBuilder.fromUriString(target)
                .queryParam("accessToken", token.accessToken())
                .queryParam("refreshToken", token.refreshToken())
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
