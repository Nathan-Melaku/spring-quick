package et.nate.backend.authentication.registration.dto;

public record RegistrationResponse(
        String access_token,
        String refresh_token
) {
}
