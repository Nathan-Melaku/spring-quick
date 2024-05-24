package et.nate.backend.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Table( name = "users",
        indexes = @Index(name = "email_ix", columnList = "email"))
@Getter
@Setter
@Builder(builderMethodName = "internalBuilder")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"roles", "verificationTokens"})
public class User extends AuditingMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "first-name")
    private String firstName;

    @Column(name = "last-name")
    private String lastName;

    private String phone;

    @Column(name = "picture-url")
    private String pictureUrl;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private SocialLoginProvider socialLoginProvider;

    @Column(name = "social-login-id")
    private String socialLoginId;

    @Embedded
    private UserAddress address;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "verification_token_id", referencedColumnName = "id")
    private VerificationToken verificationToken;

    public static UserBuilder builder(String email) {
        return User.internalBuilder().email(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return this.id == user.id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

