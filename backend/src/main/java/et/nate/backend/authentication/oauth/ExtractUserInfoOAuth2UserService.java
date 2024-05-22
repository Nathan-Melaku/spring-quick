package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.data.model.Role;
import et.nate.backend.data.model.User;
import et.nate.backend.data.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class ExtractUserInfoOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final List<UserInfoExtractor> extractors;

    public ExtractUserInfoOAuth2UserService(List<UserInfoExtractor> extractors, UserRepository userRepository) {
        this.extractors = extractors;
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oAuth2User = super.loadUser(userRequest);
        var registrationId = userRequest.getClientRegistration().getRegistrationId();
        var user = SocialLoginExtractor.extractUser(oAuth2User, extractors, registrationId);
        handleUser(user);
        return oAuth2User;
    }

    private void handleUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
            var defaultRoles = new HashSet<Role>();

            defaultRoles.add(
                    Role.builder(AuthConstants.USER_ROLE)
                            .build());

            user.setRoles(defaultRoles);
            userRepository.save(user);
        }
    }
}
