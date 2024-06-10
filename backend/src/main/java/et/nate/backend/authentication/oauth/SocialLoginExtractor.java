package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.data.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

/**

 */
public class SocialLoginExtractor {

    /**
     * An extractor that selects the right extractor from the defined beans in the project and uses it to extract the user
     * from the oAuth2User class provided.
     * @param oAuth2User user info from which we extract our needed information.
     * @param extractors beans that implement {@link UserInfoExtractor} interface.
     * @param registrationId authentication provider's registrationId
     * @return the extracted {@link User} object.
     * @throws {@link UnSupportedSocialLoginProviderException} if there is no bean that could accept the registrationId.
     */
    public static User extractUser(OAuth2User oAuth2User, List<UserInfoExtractor> extractors, String registrationId) throws UnSupportedSocialLoginProviderException{
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
