package et.nate.backend.authentication.oauth;

import lombok.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.io.Serial;

@Getter
@Setter
public class UnSupportedSocialLoginProviderException extends OAuth2AuthenticationException {
    @Serial
    private static final long serialVersionUID = 1L;
    private String registrationId;

    public UnSupportedSocialLoginProviderException(String errorCode, String registrationId) {
        super(errorCode);
        this.registrationId = registrationId;
    }

    @Override
    public String toString() {
        return "UnSupportedSocialLoginProviderException [registrationId=" + registrationId + "]";
    }
}
