package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.data.model.SocialLoginProvider;
import et.nate.backend.data.model.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * An extractor implementation for GitHub.
 */
@Service
public class GithubUserInfoExtractor implements UserInfoExtractor {

    @Override
    public User extractUserInfo(OAuth2User oAuth2User) {
        return User.builder(getAttribute(AuthConstants.GITHUB_EMAIL, oAuth2User))
                .firstName(getAttribute(AuthConstants.GITHUB_NAME, oAuth2User))
                .pictureUrl(getAttribute(AuthConstants.GITHUB_AVATAR_IMG, oAuth2User))
                .socialLoginProvider(SocialLoginProvider.GITHUB)
                .socialLoginId(oAuth2User.getName())
                .build();
    }

    @Override
    public boolean accepts(String registrationId) {
        return registrationId.equalsIgnoreCase(SocialLoginProvider.GITHUB.toString());
    }

    private String getAttribute(String attribute, OAuth2User oAuth2User) {
        var attr = oAuth2User.getAttributes().get(attribute);
        return attr != null ? attr.toString() : "";
    }
}
