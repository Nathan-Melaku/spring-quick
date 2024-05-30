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
@ToString(exclude = {"users"})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    Set<User> users;

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
