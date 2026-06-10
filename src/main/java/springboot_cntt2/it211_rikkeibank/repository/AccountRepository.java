package springboot_cntt2.it211_rikkeibank.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import springboot_cntt2.it211_rikkeibank.entity.Account;
import springboot_cntt2.it211_rikkeibank.entity.User;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByUser(User user);

    Optional<Account> findByAccountNumber(String accountNumber);
}