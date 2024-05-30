package et.nate.backend.authentication.login.dto;

public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
}
