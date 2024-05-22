package et.nate.backend.authentication.jwt;

public record TokenDTO(
        String accessToken,
        String refreshToken
) {
}
