package et.nate.backend.authentication.oauth;

import et.nate.backend.data.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * An extractor interface that should extract A {@link User} from {@link OAuth2User}
 */
public interface UserInfoExtractor {

    User extractUserInfo(OAuth2User user);

    /**
     * should return true only if the registrationId is its id and knows how to extract a User out of the
     * OAuth2User object for this provider.
     * @param registrationId
     * @return
     */
    boolean accepts(String registrationId);
}
