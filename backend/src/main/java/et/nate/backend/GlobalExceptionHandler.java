package et.nate.backend;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

/**
 * Global exception handler.
 * We want to send a descriptive error message to the client, so we intercept our exceptions here
 * and send a {@link ApplicationError}.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApplicationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        var errorMap = new HashMap<String, String>();

        for (var error : e.getBindingResult().getFieldErrors()) {
            var field = error.getField();
            var message = error.getDefaultMessage();
            errorMap.put(field, message);
        }

        if (errorMap.isEmpty()) {
            for (var error : e.getBindingResult().getAllErrors()) {
                errorMap.put(error.getObjectName(), error.getDefaultMessage());
            }
        }
        var error = new ApplicationError(e.getBody().getTitle(), e.getBody().getStatus(), errorMap);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
