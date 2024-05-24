package et.nate.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityConfigProperties {

    private String[] allowedEndpoints;
    private String[] adminEndpoints;
    private String[] userEndpoints;
}
