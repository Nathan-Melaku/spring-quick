package et.nate.backend.authentication.login;

public record OnForgotPasswordEvent(
        String email,
        String token,
        Long id
) {
}
