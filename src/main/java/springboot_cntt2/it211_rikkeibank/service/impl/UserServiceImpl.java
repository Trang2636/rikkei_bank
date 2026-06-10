package springboot_cntt2.it211_rikkeibank.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springboot_cntt2.it211_rikkeibank.dto.request.CreateUserRequest;
import springboot_cntt2.it211_rikkeibank.dto.request.UpdateUserRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.UserResponse;
import springboot_cntt2.it211_rikkeibank.entity.Role;
import springboot_cntt2.it211_rikkeibank.entity.User;
import springboot_cntt2.it211_rikkeibank.exception.BadRequestException;
import springboot_cntt2.it211_rikkeibank.exception.NotFoundException;
import springboot_cntt2.it211_rikkeibank.repository.RoleRepository;
import springboot_cntt2.it211_rikkeibank.repository.UserRepository;
import springboot_cntt2.it211_rikkeibank.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại");
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name(request.getRoleName())
                        .description(request.getRoleName())
                        .build()));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .isActive(true)
                .isKyc(false)
                .createdAt(LocalDateTime.now())
                .role(role)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public Page<UserResponse> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return userRepository.findAll(pageable)
                .map(UserResponse::from);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return UserResponse.from(user);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user id: " + id));
    }
}