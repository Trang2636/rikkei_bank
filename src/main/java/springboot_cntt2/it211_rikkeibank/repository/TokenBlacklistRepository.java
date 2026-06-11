package springboot_cntt2.it211_rikkeibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot_cntt2.it211_rikkeibank.entity.TokenBlacklist;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    boolean existsByAccessToken(String accessToken);
}