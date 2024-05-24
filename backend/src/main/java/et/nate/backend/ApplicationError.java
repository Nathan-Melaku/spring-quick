package et.nate.backend;

public record ApplicationError(
        String message,
        int code,
        Object data
){}
