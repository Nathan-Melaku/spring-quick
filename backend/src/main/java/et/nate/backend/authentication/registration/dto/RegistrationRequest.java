package et.nate.backend.authentication.registration.dto;

import et.nate.backend.authentication.registration.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatch
public record RegistrationRequest(
        String firstName,
        String lastName,
        @Email
        String email,
        @NotBlank
        @Size(min = 8, max = 30)
        String password,
        @NotBlank
        @Size(min = 8, max = 30)
        String repeatPassword,
        Address address
) {

}
