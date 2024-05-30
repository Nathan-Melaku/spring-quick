package et.nate.backend.authentication.login.dto;

import jakarta.validation.constraints.Email;

public record ForgotPasswordRequestDto(
        @Email String email
) {
}
