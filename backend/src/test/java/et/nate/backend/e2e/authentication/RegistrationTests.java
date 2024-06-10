package et.nate.backend.e2e.authentication;

import com.icegreen.greenmail.spring.GreenMailBean;
import et.nate.backend.BackendApplicationTests;
import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.authentication.registration.dto.RegistrationRequest;
import et.nate.backend.authentication.registration.dto.RegistrationResponse;
import et.nate.backend.authentication.registration.dto.VerificationResponse;
import et.nate.backend.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationTests extends BackendApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GreenMailBean greenMailBean;

    private final RegistrationRequest registrationRequest = new RegistrationRequest(
            "nathan",
            "abeje",
            "nathan@nathan.com",
            "123ertdfg456",
            "123ertdfg456",
            null);

    @Test
    void shouldRegisterUserGivenCorrectData() {
        // given
        greenMailBean.getGreenMail().setUser("test", "test");
        HttpEntity<RegistrationRequest> req = new HttpEntity<>(registrationRequest, getHeaders());

        // when
        var res = restTemplate.postForEntity("http://localhost:" + port + "/api/auth/register", req, RegistrationResponse.class);

        // then
        // assert response
        assertThat(res.getStatusCode().value()).isEqualTo(200);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().accessToken()).isNotNull();
        assertThat(res.getBody().refreshToken()).isNotNull();

        // assert user creation in db
        var user = userRepository.findByEmail("nathan@nathan.com");
        assertThat(user.isPresent()).isTrue();

        // assert verification email being sent
        var mimeMessage = greenMailBean.getReceivedMessages()[0];
        var message = "";
        try {
            message = mimeMessage.getContent().toString();
        } catch (Exception ignored) {
        }

        assertThat(message).contains("Please confirm your email address by clicking this link");
        assertThat(message).contains(user.get().getVerificationToken().getToken());

        // clean up
        userRepository.delete(user.get());
    }

    @Test
    void shouldVerifyRegisteredUserThroughAValidLinkWhenAuthenticated(){
        // given
        greenMailBean.getGreenMail().setUser("test", "test");
        HttpEntity<RegistrationRequest> registrationRequestHttpEntity = new HttpEntity<>(registrationRequest, getHeaders());
        // register a user
        var registrationResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/auth/register", registrationRequestHttpEntity, RegistrationResponse.class);
        assertThat(registrationResponse.getBody()).isNotNull();
        var token = registrationResponse.getBody().accessToken();

        assertThat(token).isNotNull();
        var headers = getHeaders();
        headers.set("Authorization", "Bearer " + token);
        // todo should also add a cookie to pass
        // get verification token
        var user = userRepository.findByEmail("nathan@nathan.com");
        assertThat(user.isPresent()).isTrue();
        HttpEntity<String> verificationRequest = new HttpEntity<>(null, headers);
        // when
        var verificationResponse = restTemplate.postForEntity("http://localhost:" + port + "/api/verify?token=" + user.get().getVerificationToken().getToken() , verificationRequest, VerificationResponse.class);

        // Then
        assertThat(verificationResponse.getStatusCode().value()).isEqualTo(200);
        assertThat(verificationResponse.getBody()).isNotNull();
        assertThat(verificationResponse.getBody().message()).isEqualTo(AuthConstants.VERIFICATION_COMPLETED);
    }

    @Test
    void shouldResendEmailWhenAuthenticated(){

    }

}
