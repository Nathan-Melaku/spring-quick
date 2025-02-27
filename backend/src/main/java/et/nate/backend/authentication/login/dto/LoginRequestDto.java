package et.nate.backend.authentication.login.dto;

import jakarta.validation.constraints.Email;

public record LoginRequestDto(
        @Email
        String email,
        String password
) {
}
