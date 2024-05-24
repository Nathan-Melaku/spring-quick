package et.nate.backend.authentication.registration;

public class RegistrationVerificationException extends Exception{

    public RegistrationVerificationException(String error) {
        super(error);
    }
}
