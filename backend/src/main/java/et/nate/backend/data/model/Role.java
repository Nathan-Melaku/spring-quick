package et.nate.backend.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity(name = "roles")
@Getter
@Setter
@Builder( builderMethodName = "internalBuilder")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"users","privileges"})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    Set<User> users;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_privilege",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    private Set<Privilege> privileges;

    public static RoleBuilder builder(String name) {
        return Role.internalBuilder().name(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return this.id == role.id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
