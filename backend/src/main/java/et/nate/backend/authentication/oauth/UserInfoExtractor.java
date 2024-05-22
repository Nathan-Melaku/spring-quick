package et.nate.backend.authentication.oauth;

import et.nate.backend.data.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserInfoExtractor {

    User extractUserInfo(OAuth2User user);
    boolean accepts(String registrationId);
}
