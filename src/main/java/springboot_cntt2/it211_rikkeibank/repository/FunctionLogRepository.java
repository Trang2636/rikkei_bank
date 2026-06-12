package springboot_cntt2.it211_rikkeibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot_cntt2.it211_rikkeibank.entity.FunctionLog;

public interface FunctionLogRepository extends JpaRepository<FunctionLog, Long> {
}
