package et.nate.backend.authentication.exceptions;

public class CustomJwtValidationException extends Throwable {

    public CustomJwtValidationException(String reason, Exception e) {
        super(reason, e);
    }
    public CustomJwtValidationException(String reason) {
        super(reason);
    }
}
