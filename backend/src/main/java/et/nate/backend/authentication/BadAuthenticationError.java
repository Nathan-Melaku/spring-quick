package et.nate.backend.authentication;

public record BadAuthenticationError(
        String message,
        int code,
        Object data
){}
