package et.nate.backend.authentication;

import et.nate.backend.ApplicationError;
import et.nate.backend.authentication.jwt.CustomJwtValidationException;
import et.nate.backend.authentication.registration.RegistrationVerificationException;
import et.nate.backend.authentication.registration.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthenticationExceptionHandler {
    /**
     * JWT validation might fail for access token and refresh token.
     * @param e: exception
     * @return Response Entity with UNAUTHORIZED
     */
    @ExceptionHandler(CustomJwtValidationException.class)
    public ResponseEntity<ApplicationError> handleCustomJwtValidationException(CustomJwtValidationException e) {

        var detail = e.getDetail() != null ? e.getDetail() : e.getMessage();
        var error = new ApplicationError(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), detail);

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApplicationError> handleUserAlreadyExistsException(UserAlreadyExistsException e) {

        var error = new ApplicationError(HttpStatus.CONFLICT.getReasonPhrase(), HttpStatus.CONFLICT.value(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApplicationError> handleUsernameNotFoundException(UsernameNotFoundException e) {

        var error = new ApplicationError(HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApplicationError> handleBadCredentialsException(BadCredentialsException e) {

        var error = new ApplicationError(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RegistrationVerificationException.class)
    public ResponseEntity<ApplicationError> handleRegistrationVerificationException(RegistrationVerificationException e) {

        var error = new ApplicationError(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
