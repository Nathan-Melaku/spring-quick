package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.data.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public class SocialLoginExtractor {

    public static User extractUser(OAuth2User oAuth2User, List<UserInfoExtractor> extractors, String registrationId) {
        var infoExtractor =  extractors.stream()
                .filter(extractor -> extractor.accepts(registrationId))
                .findFirst();
        if (infoExtractor.isEmpty()) {
            throw new UnSupportedSocialLoginProviderException(AuthConstants.UNSUPPORTED_AUTH_PROVIDER,
                    registrationId);
        }
        return infoExtractor.get().extractUserInfo(oAuth2User);
    }
}
