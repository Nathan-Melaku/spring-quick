package et.nate.backend.authentication.registration;

import et.nate.backend.authentication.AuthConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAlreadyExistsException extends Exception{
    @Override
    public String getMessage() {
        return AuthConstants.USER_ALREADY_EXISTS_ERROR;
    }
}
