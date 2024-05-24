package et.nate.backend.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity(name = "verification_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    private Instant expiryDate;

/*    @OneToOne(mappedBy = "verification_token")
    private User user;*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        VerificationToken verificationToken = (VerificationToken) o;
        return this.id == verificationToken.id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
