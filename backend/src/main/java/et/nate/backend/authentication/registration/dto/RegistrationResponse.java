package et.nate.backend.authentication.registration.dto;

public record RegistrationResponse(
        String accessToken,
        String refreshToken
) {
}
