package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.jwt.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class ExchangeTokenOAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${app.oauth.redirect-uri}")
    private String redirectUri;

    private final TokenService tokenService;

    public ExchangeTokenOAuthSuccessHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

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
        var token = tokenService.generateToken(authentication);
        // TODO store the token in DB
        target = UriComponentsBuilder.fromUriString(target).queryParam("token", token).build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
