package et.nate.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public class BackendApplicationTests {

    @LocalServerPort
    protected int port;

    protected TestRestTemplate restTemplate = new TestRestTemplate();

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.2");

    @Test
    void contextLoads() {
    }

    protected HttpHeaders setCsrfCookie() {
        var httpHeaders = new HttpHeaders();
        var req_headers = restTemplate.headForHeaders("http://localhost:" + port + "/csrf");
        var cookies = req_headers.get("Set-Cookie");
        assertThat(cookies).isNotNull();
        var XSRFToken = cookies.getFirst().split(";")[0].split("=")[1];
        httpHeaders.set("X-XSRF-TOKEN", XSRFToken);
        httpHeaders.set("Cookie", cookies.getFirst());
        return httpHeaders;
    }
}
