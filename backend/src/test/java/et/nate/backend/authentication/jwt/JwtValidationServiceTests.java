package et.nate.backend.authentication.jwt;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import et.nate.backend.data.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class JwtValidationServiceTests {

    @Mock
    private JWKSource<SecurityContext> jwkSource;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private JwtValidationService subject;

    // test for validation of correct token

    // if signature is wrong doesn't validate

    // if token header is not Bearer doesn't validate

    // if token doesn't contain all claims doesn't validate
}
