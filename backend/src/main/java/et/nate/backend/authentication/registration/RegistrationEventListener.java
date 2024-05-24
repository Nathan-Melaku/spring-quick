package et.nate.backend.authentication.registration;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEventListener {
    @EventListener
    public void OnRegistrationCompleted(RegistrationCompletedEvent event) {
        if (event != null){
            // send email
            System.out.println("REGISTRATION COMPLETED sending email. " + "http://localhost:8080/api/verify?token=" + event.getToken() + " To: " + event.getEmail());
        }
    }
}

