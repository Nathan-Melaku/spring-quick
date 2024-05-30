package et.nate.backend.authentication.jwt;

public record TokenResult(
        String accessToken,
        String refreshToken,
        // String value to store in cookie. its hash must match the context claim in the jwt.
        String userContextCookie,
        int accessExpiresAt,
        int refreshExpiresAt
) {
}
