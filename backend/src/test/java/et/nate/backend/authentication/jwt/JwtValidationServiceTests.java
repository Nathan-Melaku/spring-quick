package et.nate.backend.authentication.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import et.nate.backend.authentication.AuthConstants;
import et.nate.backend.data.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.testcontainers.shaded.org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.testcontainers.shaded.org.bouncycastle.openssl.PEMParser;
import org.testcontainers.shaded.org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.FileReader;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtValidationServiceTests {

    private static JWKSource<SecurityContext> jwkSource;

    @Mock
    private static UserRepository userRepository;
    @Mock
    private static PasswordEncoder passwordEncoder;

    private static JwtValidationService subject;

    @BeforeAll
    static void setUp() {
        jwkSource = getJwkSource();
        subject = new JwtValidationService(
                jwkSource,
                userRepository,
                passwordEncoder
        );
    }

    // if signature is wrong doesn't validate

    // if token header is not Bearer doesn't validate

    // if token doesn't contain all claims doesn't validate

    @Test
    void shouldValidateJwtGivenAValidJwt() {
        // Given
        var token = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiMSIsIl9fU2VjdXJlLVVzZXJDb250ZXh0IjoiNWY3OWI1ZmY0OTc5ODZmNzVhYWZhNjgzZjMwMTFjMDBhYjY4MzQ5NDJlZTRlOTMyMTc1YWY0MWE1NmE3MmJjZCIsImV4cCI6MTcxODAwMjg3NSwiaWF0IjoxNzE3OTk5Mjc1LCJzY29wZSI6WyJST0xFX1VOVkVSSUZJRUQiXX0.qWeGoJZWe8CkRg_UBdowHVM2hyV0ww_nXcd5rk67zJHJofx4BvvQj-KLMxAFX6trvaBQnZQKOtKN5EE3T0e0zZ19mc_Iob9TB2HjAGyU-H5vxL-QBZabRNolqFMqIs0HXp1-41TwlPWh6Hqcw7EGZaIk1rCBdgqbH6CXQj4OUqd6PDkYMVmQSovnyBTEPsBP_mk-v-bi1UpGIcWN4xms6pp3khDh5hewvzi_4AbopfrZleW2YYQfSsGqg52naZGYjY4-3HOQA5B0OC2acfqtMKZdEPEbP0SeqvOiBf1I8Pk4SfMkh9v28Wp08LoW_bKRH9PBT_J4hGF1ywCCQWDW-M5FEyjA5BjDyMwXKEUIppJ7suCGRM-CvjaChWma2kYXuZe9NoYx2hcRHE8GBwJfO_30mEI9XDTYY6zW1vrMl0EFIReqTDPmxvQP9_5anE5dTiFvK_gIbOMGXjuDk9QM3E7j8Rvaj358t1F9clcyjObKsGelhfDSgmKbI7-1Az4v3oBEwMSdnrNjlU6mnfcqBbm8tXxt8nlMhRHlEi56UukFALhGzlIZrVaADJ__FtaaqsJGWu02agHNaMQsL7zFsgc-IO1De6ck8-VZ1UtuP6aaKuEovji5rnGQLXEatux51a4Ry2bXjrDasXiqMQUJUUhb0OTdiOQ3chAl3KoBx6c";
        var claims = new HashSet<>(Arrays.asList(
                JWTClaimNames.SUBJECT,
                JWTClaimNames.ISSUED_AT,
                AuthConstants.SCOPE,
                AuthConstants.USER_CONTEXT_COOKIE,
                JWTClaimNames.EXPIRATION_TIME));
        JWTClaimsSet claimsSet = null;

        try {
            // When
            claimsSet = subject.validate(token, claims);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Then
        assertThat(claimsSet).isNotNull();
    }

    private static RSAPublicKey getPubKey() {
        try (var reader = new FileReader("src/main/resources/certs/public.pem")) {
            var pemParser = new PEMParser(reader);
            var converter = new JcaPEMKeyConverter();
            var publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
            return (RSAPublicKey) converter.getPublicKey(publicKeyInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static RSAPrivateKey getPrivateKey() {
        try (var reader = new FileReader("src/main/resources/certs/private.pem")) {
            var pemParser = new PEMParser(reader);
            var converter = new JcaPEMKeyConverter();
            var privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
            return (RSAPrivateKey) converter.getPrivateKey(privateKeyInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JWKSource<SecurityContext> getJwkSource() {
        var publicKey = getPubKey();
        var privateKey = getPrivateKey();
        assert publicKey != null;
        var jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
        return new ImmutableJWKSet<>(new JWKSet(jwk));
    }
}
