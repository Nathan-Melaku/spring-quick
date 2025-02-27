package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.jwt.JwtMintingService;
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

import static et.nate.backend.authentication.AuthUtils.setCookies;

/**
 * A success handler class that will be triggered upon a successful oauth login with a social login method.
 * it should generate tokens and adds them to the redirect link. and also sets the context cookie need for login.
 * And then redirect the user to the redirect link provided in the configuration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeTokenOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth.redirect-uri}")
    private String redirectUri;

    private final JwtMintingService jwtMintingService;

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
                          Authentication authentication) throws IOException {
        var target = redirectUri.isBlank() ? determineTargetUrl(request, response) : redirectUri;
        var token = jwtMintingService.generateAccessToken(authentication, 0);
        setCookies(response, token);

        target = UriComponentsBuilder.fromUriString(target)
                .queryParam(AuthConstants.ACCESS_TOKEN, token.accessToken())
                .queryParam(AuthConstants.REFRESH_TOKEN, token.refreshToken())
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
