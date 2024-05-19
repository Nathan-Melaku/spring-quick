package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.model.User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface UserInfoExtractor {

    public User extractUserInfo(OAuth2User user);
    public boolean accepts(OAuth2UserRequest userRequest);
}
