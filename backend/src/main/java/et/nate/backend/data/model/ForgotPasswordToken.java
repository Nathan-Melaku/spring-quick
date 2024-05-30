package et.nate.backend.data.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity(name = "forgot_password_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String token;

    private Instant expiryDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ForgotPasswordToken forgotPasswordToken = (ForgotPasswordToken) o;
        return this.id == forgotPasswordToken.id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
