package et.nate.backend.authentication.registration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendRegistrationEvent extends RegistrationCompletedEvent {

    public ResendRegistrationEvent(Object source, String token, String email) {
        super(source, token, email);
    }
}
