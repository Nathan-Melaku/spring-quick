package et.nate.backend.authentication.registration;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationEventListener {

    private final JavaMailSender mailSender;

    @EventListener
    public void OnRegistrationCompleted(RegistrationCompletedEvent event) throws MessagingException {
        if (event != null) {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(event.getEmail());
            helper.setSubject("Registration Confirmation");
            helper.setText(
                    "<h1> Please confirm your email address by clicking this link </h1>"
                            + "<a href=\"http://localhost:8080/api/verify?token=" + event.getToken()
                            + "\" target=\"_blank\">Click here to verify your email</a>", true);

            mailSender.send(mimeMessage);
        }
    }
}

