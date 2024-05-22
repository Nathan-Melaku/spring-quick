package et.nate.backend.authentication.jwt;


import et.nate.backend.authentication.BadAuthenticationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class JwtControllerAdvice {
    @ExceptionHandler({CustomJwtValidationException.class})
    public ResponseEntity<BadAuthenticationError> handleCustomJwtValidationException(CustomJwtValidationException e) {
        var error = new BadAuthenticationError(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), null);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}
