package et.nate.backend.authentication.login.dto;

public record LoginRequestDto(
        String email,
        String password
) {
}
