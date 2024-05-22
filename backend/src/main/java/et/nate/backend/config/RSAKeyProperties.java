package et.nate.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.rsa")
public class RSAKeyProperties {
    RSAPublicKey rsaPublicKey;
    RSAPrivateKey rsaPrivateKey;
}
