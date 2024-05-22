package et.nate.backend.authentication.dto;

public record BadAuthenticationError(
        String message,
        int code,
        Object data
){}
