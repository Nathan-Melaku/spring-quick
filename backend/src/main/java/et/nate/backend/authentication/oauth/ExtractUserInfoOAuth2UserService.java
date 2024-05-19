package et.nate.backend.authentication.oauth;

import et.nate.backend.authentication.exceptions.UnSupportedSocialLoginProviderException;
import et.nate.backend.authentication.model.User;
import et.nate.backend.authentication.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

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
        OAuth2User oAuth2User = super.loadUser(userRequest);
        var infoExtractor =  extractors.stream()
                .filter(extractor -> extractor.accepts(userRequest))
                .findFirst();
        if (infoExtractor.isEmpty()) {
            throw new UnSupportedSocialLoginProviderException("Unsupported authentication provider",
                    userRequest.getClientRegistration().getRegistrationId());
        }
        infoExtractor.ifPresent(extractor -> {
            handleUser(extractor.extractUserInfo(oAuth2User));
        });
        return oAuth2User;
    }

    private void handleUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
            userRepository.save(user);
        }
    }
}
