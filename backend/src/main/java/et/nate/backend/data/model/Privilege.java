package et.nate.backend.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity(name = "privileges")
@Getter
@Setter
@Builder(builderMethodName = "internalBuilder")
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"roles"})
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Set<Role> roles;

    public static PrivilegeBuilder builder(String name) {
        return internalBuilder().name(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Privilege privilege = (Privilege) o;
        return this.id == privilege.id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
