package et.nate.backend.config;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.BadAuthenticationEntryPoint;
import et.nate.backend.authentication.csrf.CsrfCookieFilter;
import et.nate.backend.authentication.csrf.SpaCsrfTokenRequestHandler;
import et.nate.backend.authentication.jwt.JwtAuthenticationFilter;
import et.nate.backend.authentication.oauth.ExchangeTokenOAuthSuccessHandler;
import et.nate.backend.authentication.oauth.ExtractUserInfoOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ExtractUserInfoOAuth2UserService extractUserInfoOAuth2UserService;
    private final ExchangeTokenOAuthSuccessHandler exchangeTokenOAuthSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityConfigProperties securityConfigProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        var allowedEndpoints = securityConfigProperties.getAllowedEndpoints();
        var adminEndpoints = securityConfigProperties.getAdminEndpoints();
        var userEndpoints = securityConfigProperties.getUserEndpoints();

        httpSecurity
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(allowedEndpoints).permitAll()
                                .requestMatchers(userEndpoints).hasAnyRole(AuthConstants.USER, AuthConstants.ADMIN)
                                .requestMatchers(adminEndpoints).hasRole(AuthConstants.ADMIN)
                                .anyRequest().authenticated())
                .exceptionHandling(handler ->
                        handler.authenticationEntryPoint(new BadAuthenticationEntryPoint()))
                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(config -> config.userService(extractUserInfoOAuth2UserService))
                                .successHandler(exchangeTokenOAuthSuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter.class)
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
