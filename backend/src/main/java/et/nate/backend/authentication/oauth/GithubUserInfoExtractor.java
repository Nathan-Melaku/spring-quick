package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.model.SocialLoginProvider;
import et.nate.backend.authentication.model.User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class GithubUserInfoExtractor implements UserInfoExtractor {
    @Override
    public User extractUserInfo(OAuth2User oAuth2User) {
        return User.builder(getAttribute("email", oAuth2User))
                .firstName(getAttribute("name", oAuth2User))
                .pictureUrl(getAttribute("avatar_url", oAuth2User))
                .socialLoginProvider(SocialLoginProvider.GITHUB)
                .socialLoginId(oAuth2User.getName())
                .build();
    }

    @Override
    public boolean accepts(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getRegistrationId().equalsIgnoreCase("github");
    }

    private String getAttribute(String attribute, OAuth2User oAuth2User) {
        var attr = oAuth2User.getAttributes().get(attribute);
        return attr != null ? attr.toString() : "";
    }
}
