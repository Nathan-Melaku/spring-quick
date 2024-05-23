package et.nate.backend.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity(name = "refresh-token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 4096)
    private String token;

    private Instant issuedAt;
    private Instant expiresAt;

   @Override
   public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || this.getClass() != o.getClass()) return false;
       RefreshToken refreshToken = (RefreshToken) o;
       return this.id == refreshToken.id;
   }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
