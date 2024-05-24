package et.nate.backend.authentication.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CustomJwtValidationException extends Throwable {

    private String detail;

    public CustomJwtValidationException(String detail, String reason) {
        super(reason);
    }
    public CustomJwtValidationException(String reason) {
        super(reason);
    }
}
