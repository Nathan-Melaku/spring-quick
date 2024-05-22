package et.nate.backend.authentication.jwt;

import java.time.Instant;

public record TokenResult(
        String accessToken,
        String refreshToken,
        Instant createdAt,
        Instant expireAt
) { }
