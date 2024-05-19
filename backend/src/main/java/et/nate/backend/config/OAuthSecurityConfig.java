package et.nate.backend.config;

import et.nate.backend.authentication.oauth.ExtractUserInfoOAuth2UserService;
import et.nate.backend.authentication.oauth.ExchangeTokenOAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class OAuthSecurityConfig {

    private final ExtractUserInfoOAuth2UserService extractUserInfoOAuth2UserService;
    private final ExchangeTokenOAuthSuccessHandler exchangeTokenOAuthSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated())
                .oauth2Login(oauth -> {
                    oauth.userInfoEndpoint(config -> config.userService(extractUserInfoOAuth2UserService))
                            .successHandler(exchangeTokenOAuthSuccessHandler);
                });
        return httpSecurity.build();
    }
}
