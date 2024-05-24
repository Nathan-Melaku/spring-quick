package et.nate.backend.authentication.registration;

import et.nate.backend.authentication.registration.dto.RegistrationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, RegistrationRequest> {
    @Override
    public void initialize(PasswordMatch p) {
            ConstraintValidator.super.initialize(p);
    }

    @Override
    public boolean isValid(RegistrationRequest registrationRequest, ConstraintValidatorContext context) {
        var password = registrationRequest.password();
        var repeatPassword = registrationRequest.repeatPassword();
        return password != null && password.equals(repeatPassword);
    }
}
