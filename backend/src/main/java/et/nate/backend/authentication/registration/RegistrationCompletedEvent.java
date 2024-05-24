package et.nate.backend.authentication.registration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompletedEvent extends ApplicationEvent {
    private String token;
    private String email;

    public RegistrationCompletedEvent(Object source, String token, String email) {
        super(source);
        this.token = token;
        this.email = email;
    }
}
