package et.nate.backend.data.repository;

import et.nate.backend.data.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    public List<RefreshToken> findRefreshTokenByToken(String token);
}
