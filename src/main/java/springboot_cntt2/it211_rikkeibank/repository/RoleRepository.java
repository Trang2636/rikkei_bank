package springboot_cntt2.it211_rikkeibank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot_cntt2.it211_rikkeibank.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);
}
