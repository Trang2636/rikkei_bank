package springboot_cntt2.it211_rikkeibank.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import springboot_cntt2.it211_rikkeibank.entity.KycProfile;
import springboot_cntt2.it211_rikkeibank.entity.User;

import java.util.Optional;

public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {

    Optional<KycProfile> findByUser(User user);

    boolean existsByIdNumber(String idNumber);
}
