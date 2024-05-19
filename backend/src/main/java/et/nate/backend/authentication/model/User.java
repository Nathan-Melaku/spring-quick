package et.nate.backend.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity(name = "users")
@Builder(builderMethodName = "internalBuilder")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles;

    public static UserBuilder builder(String email){
        return User.internalBuilder().email(email);
    }
}
