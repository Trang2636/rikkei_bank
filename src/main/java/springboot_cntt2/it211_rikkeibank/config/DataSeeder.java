package springboot_cntt2.it211_rikkeibank.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import springboot_cntt2.it211_rikkeibank.entity.Role;
import springboot_cntt2.it211_rikkeibank.entity.User;
import springboot_cntt2.it211_rikkeibank.repository.RoleRepository;
import springboot_cntt2.it211_rikkeibank.repository.UserRepository;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN", "Quản trị hệ thống");
        Role staffRole = createRoleIfNotExists("ROLE_STAFF", "Giao dịch viên");
        createRoleIfNotExists("ROLE_CUSTOMER", "Khách hàng");

        createUserIfNotExists("admin", "admin@gmail.com", "0123456789", adminRole);
        createUserIfNotExists("staff", "staff@gmail.com", "0123456788", staffRole);
    }

    private Role createRoleIfNotExists(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(name)
                        .description(description)
                        .build()));
    }

    private void createUserIfNotExists(String username, String email, String phone, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("123456"))
                    .phoneNumber(phone)
                    .email(email)
                    .isActive(true)
                    .isKyc(true)
                    .createdAt(LocalDateTime.now())
                    .role(role)
                    .build();

            userRepository.save(user);
        }
    }
}