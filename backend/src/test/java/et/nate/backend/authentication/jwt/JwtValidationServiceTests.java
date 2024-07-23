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

    @Mock
    private static UserRepository userRepository;
    @Mock
    private static PasswordEncoder passwordEncoder;

    private static JwtValidationService subject;

    @BeforeAll
    static void setUp() {
        JWKSource<SecurityContext> jwkSource = getJwkSource();
        subject = new JwtValidationService(
                jwkSource,
                userRepository,
                passwordEncoder
        );
    }

    // if signature is wrong doesn't validate
    @Test
    void shouldNotValidateJwtGivenAnInvalidJwt() {
        // Given
        var token = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiMSIsIl9fU2VjdXJlLVVzZXJDb250ZXh0IjoiNTI3Y2ZiMDcxMDMxNTFkMGE4ZmQwY2I1MTg2OTZiZGJiNTZiOGVmYjdlOGU2OWI1NjVjYWJhMzFmZTIzNjBlYiIsImV4cCI6MzAxNzMwMTk1NCwiaWF0IjoxNzIxMzAxOTU0LCJzY29wZSI6WyJST0xFX1VOVkVSSUZJRUQiXX0.mm3CCJEWS5sQcwR21WRoD_87naL056DbBtEuB6t-RWJmGaUvdTvr1m9qYCwOnxIHKTPDZuvPSHY720A8M15bSL99KgWpRyntG2yQXu670DNgGx1MsvtqyHotBXFCiIgRzdUJ1U6QiMLnvJNCqCUztFLMgiTXkvWBT_lwU3WSfllbgkhuaJsokMhJJJSYruShbnTUqv5NY3khptyTgc4EX7Mkbe_c0OS-FWHiv4bAazJeU9ysadCMcqB3Yj2vhreOjaTmFBiJ9NyJF2U8mYTLzb7U6ZkJdek_PED3xTaez5cd0vrFR9iNhR6Xvwu3jYhIBt3gFxUsMpVtNv0Gp_4vIlnxwpYNxfBa7NywkFsaYf6esCGL0VkB2jZUsf8m1VsWWaTHR6X0wnayzS3KjEiAqGFW92pD9UBYR4dlG5HToAO5PFRz83yH4AjRVJfNPaUtOFB64ppxH9W22-bh0GuUs9dePaLuOqF4HFaOjrkN21TOaXPFi_spOtYWotvUyvDZl41BCPETfuQ1pIqTNqVdZvMknru7gRHw7bNl6n7J2yte84GcYkD7HPg5_O3ZeT_fAZoc149ikF-0SOe6vxsfBrud73OprY_4iQQfd8zBHTMGQR6bfIkuo2LSJ8svxECzGc1oQZRGcTYffxZOYCyoFSfJWACNB6Ob-d3PrpXJaA1";
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
        assertThat(claimsSet).isNull();
    }

    // if token header is not Bearer doesn't validate
    @Test
    void shouldNotValidateJwtGivenANonBearerJwt() {
        // Given
        var token = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiMSIsIl9fU2VjdXJlLVVzZXJDb250ZXh0IjoiNTI3Y2ZiMDcxMDMxNTFkMGE4ZmQwY2I1MTg2OTZiZGJiNTZiOGVmYjdlOGU2OWI1NjVjYWJhMzFmZTIzNjBlYiIsImV4cCI6MzAxNzMwMTk1NCwiaWF0IjoxNzIxMzAxOTU0LCJzY29wZSI6WyJST0xFX1VOVkVSSUZJRUQiXX0.mm3CCJEWS5sQcwR21WRoD_87naL056DbBtEuB6t-RWJmGaUvdTvr1m9qYCwOnxIHKTPDZuvPSHY720A8M15bSL99KgWpRyntG2yQXu670DNgGx1MsvtqyHotBXFCiIgRzdUJ1U6QiMLnvJNCqCUztFLMgiTXkvWBT_lwU3WSfllbgkhuaJsokMhJJJSYruShbnTUqv5NY3khptyTgc4EX7Mkbe_c0OS-FWHiv4bAazJeU9ysadCMcqB3Yj2vhreOjaTmFBiJ9NyJF2U8mYTLzb7U6ZkJdek_PED3xTaez5cd0vrFR9iNhR6Xvwu3jYhIBt3gFxUsMpVtNv0Gp_4vIlnxwpYNxfBa7NywkFsaYf6esCGL0VkB2jZUsf8m1VsWWaTHR6X0wnayzS3KjEiAqGFW92pD9UBYR4dlG5HToAO5PFRz83yH4AjRVJfNPaUtOFB64ppxH9W22-bh0GuUs9dePaLuOqF4HFaOjrkN21TOaXPFi_spOtYWotvUyvDZl41BCPETfuQ1pIqTNqVdZvMknru7gRHw7bNl6n7J2yte84GcYkD7HPg5_O3ZeT_fAZoc149ikF-0SOe6vxsfBrud73OprY_4iQQfd8zBHTMGQR6bfIkuo2LSJ8svxECzGc1oQZRGcTYffxZOYCyoFSfJWACNB6Ob-d3PrpXJaAY";
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
        assertThat(claimsSet).isNull();
    }

    // if token doesn't contain all claims doesn't validate
    @Test
    void shouldValidateJwtGivenAValidJwt() {
        // Given
        var token = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoiMSIsIl9fU2VjdXJlLVVzZXJDb250ZXh0IjoiNTI3Y2ZiMDcxMDMxNTFkMGE4ZmQwY2I1MTg2OTZiZGJiNTZiOGVmYjdlOGU2OWI1NjVjYWJhMzFmZTIzNjBlYiIsImV4cCI6MzAxNzMwMTk1NCwiaWF0IjoxNzIxMzAxOTU0LCJzY29wZSI6WyJST0xFX1VOVkVSSUZJRUQiXX0.mm3CCJEWS5sQcwR21WRoD_87naL056DbBtEuB6t-RWJmGaUvdTvr1m9qYCwOnxIHKTPDZuvPSHY720A8M15bSL99KgWpRyntG2yQXu670DNgGx1MsvtqyHotBXFCiIgRzdUJ1U6QiMLnvJNCqCUztFLMgiTXkvWBT_lwU3WSfllbgkhuaJsokMhJJJSYruShbnTUqv5NY3khptyTgc4EX7Mkbe_c0OS-FWHiv4bAazJeU9ysadCMcqB3Yj2vhreOjaTmFBiJ9NyJF2U8mYTLzb7U6ZkJdek_PED3xTaez5cd0vrFR9iNhR6Xvwu3jYhIBt3gFxUsMpVtNv0Gp_4vIlnxwpYNxfBa7NywkFsaYf6esCGL0VkB2jZUsf8m1VsWWaTHR6X0wnayzS3KjEiAqGFW92pD9UBYR4dlG5HToAO5PFRz83yH4AjRVJfNPaUtOFB64ppxH9W22-bh0GuUs9dePaLuOqF4HFaOjrkN21TOaXPFi_spOtYWotvUyvDZl41BCPETfuQ1pIqTNqVdZvMknru7gRHw7bNl6n7J2yte84GcYkD7HPg5_O3ZeT_fAZoc149ikF-0SOe6vxsfBrud73OprY_4iQQfd8zBHTMGQR6bfIkuo2LSJ8svxECzGc1oQZRGcTYffxZOYCyoFSfJWACNB6Ob-d3PrpXJaAY";
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
