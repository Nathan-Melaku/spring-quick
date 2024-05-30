package et.nate.backend.authentication.login;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForgotPasswordEventListener {
    private final JavaMailSender mailSender;
    // TODO enhance the email text
    @EventListener
    public void OnForgotPasswordEvent(OnForgotPasswordEvent event) throws MessagingException {
        if (event != null) {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(event.email());
            helper.setSubject("Forgot Password Reset");
            helper.setText(
                    "<h1> Reset your password </h1>"
                            + "<a href=\"http://localhost:8080/api/auth/reset?token=" + event.token() + "&id=" + event.id()
                            + "\" target=\"_blank\">Click here to Reset your password</a>", true);

            mailSender.send(mimeMessage);
        }
    }
}
