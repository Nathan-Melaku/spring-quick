package et.nate.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

   private final CorsConfigProperties corsConfigProperties;

    public CorsConfig(CorsConfigProperties corsConfigProperties) {
        this.corsConfigProperties = corsConfigProperties;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        var urlCorsConfiguration = new UrlBasedCorsConfigurationSource();
        configuration.setAllowedMethods(corsConfigProperties.getAllowedMethods());
        configuration.setAllowedOrigins(corsConfigProperties.getAllowedOrigins());
        configuration.setAllowedHeaders(List.of("*"));
        urlCorsConfiguration.registerCorsConfiguration("/**", configuration);
        return urlCorsConfiguration;
    }
}
