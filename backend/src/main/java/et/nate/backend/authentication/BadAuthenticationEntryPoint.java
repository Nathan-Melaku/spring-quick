package et.nate.backend.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import java.io.IOException;


import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public final class BadAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        var error = (String ) request.getAttribute(AuthConstants.ERROR);
        var errorObject = new BadAuthenticationError(
                error,
                HttpServletResponse.SC_UNAUTHORIZED,
                null);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (error != null) {
            ObjectMapper mapper = new ObjectMapper();
            var stream = response.getOutputStream();
            mapper.writeValue(stream, errorObject);
            stream.flush();
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, AuthConstants.ACCESS_DENIED);
        }
    }
}


