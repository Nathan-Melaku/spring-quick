package et.nate.backend.authentication;

import et.nate.backend.authentication.exceptions.UnSupportedSocialLoginProviderException;
import et.nate.backend.authentication.model.User;
import et.nate.backend.authentication.oauth.UserInfoExtractor;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public class AuthUtils {

    public static final String ISSUER = "self";

    public static User extractUser(OAuth2User oAuth2User, List<UserInfoExtractor> extractors, String registrationId) {
        User user = null;
        var infoExtractor =  extractors.stream()
                .filter(extractor -> extractor.accepts(registrationId))
                .findFirst();
        if (infoExtractor.isEmpty()) {
            throw new UnSupportedSocialLoginProviderException("Unsupported authentication provider",
                    registrationId);
        }
        return infoExtractor.get().extractUserInfo(oAuth2User);
    }
}
