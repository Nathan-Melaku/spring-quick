package et.nate.backend.authentication.login.dto;

public record LoginResponseDto(
        String access_token,
        String refresh_token
) {
}
