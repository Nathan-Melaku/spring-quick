package et.nate.backend.authentication.dto;

public record TokenDTO(
        String accessToken,
        String refreshToken
) {
}
