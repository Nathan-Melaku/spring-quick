package et.nate.backend.e2e.authentication;

import et.nate.backend.BackendApplicationTests;
import org.junit.jupiter.api.Test;

public class JwtTests extends BackendApplicationTests {

    @Test
    public void shouldAuthenticateGivenValidJwt() {

    }

    @Test
    public void shouldDenyAccessGivenInvalidJwt() {}

    @Test
    public void shouldRefreshTokenGivenValidJwt() {}

    @Test
    public void shouldNotRefreshTokenGivenInvalidJwt() {}

    @Test
    public void shouldNotRefreshTokenGivenARepeatedJwt() {}

}
