package et.nate.backend.authentication.repository;

import et.nate.backend.authentication.model.Role;
import et.nate.backend.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Set<Role> findRolesByEmail(String email);
}
