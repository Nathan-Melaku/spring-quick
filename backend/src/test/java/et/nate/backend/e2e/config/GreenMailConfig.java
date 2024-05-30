package et.nate.backend.e2e.config;

import com.icegreen.greenmail.spring.GreenMailBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GreenMailConfig {

    @Bean
    public GreenMailBean getGreenMail() {
        var greenMail =  new GreenMailBean();
        greenMail.setAutostart(true);
        greenMail.setSmtpProtocol(true);
        greenMail.setPortOffset(3000);
        greenMail.setHostname("127.0.0.1");
        return greenMail;
    }
}
