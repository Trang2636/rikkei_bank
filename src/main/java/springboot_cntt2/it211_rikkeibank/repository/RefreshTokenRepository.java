package springboot_cntt2.it211_rikkeibank.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import springboot_cntt2.it211_rikkeibank.entity.RefreshToken;
import springboot_cntt2.it211_rikkeibank.entity.User;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserAndRevokedFalse(User user);
}
