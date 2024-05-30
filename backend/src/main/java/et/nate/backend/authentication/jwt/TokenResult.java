package et.nate.backend.authentication.jwt;

public record TokenResult(
        String accessToken,
        String refreshToken,
        String userContextCookie,
        int accessExpiresAt,
        int refreshExpiresAt
) { }
