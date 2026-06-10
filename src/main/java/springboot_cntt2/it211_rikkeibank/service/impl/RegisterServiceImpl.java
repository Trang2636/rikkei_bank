package springboot_cntt2.it211_rikkeibank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_cntt2.it211_rikkeibank.dto.request.RegisterRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.AccountResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.KycResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.RegisterResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.UserResponse;
import springboot_cntt2.it211_rikkeibank.entity.Account;
import springboot_cntt2.it211_rikkeibank.entity.KycProfile;
import springboot_cntt2.it211_rikkeibank.entity.Role;
import springboot_cntt2.it211_rikkeibank.entity.User;
import springboot_cntt2.it211_rikkeibank.enums.KycStatus;
import springboot_cntt2.it211_rikkeibank.exception.BadRequestException;
import springboot_cntt2.it211_rikkeibank.repository.AccountRepository;
import springboot_cntt2.it211_rikkeibank.repository.KycProfileRepository;
import springboot_cntt2.it211_rikkeibank.repository.RoleRepository;
import springboot_cntt2.it211_rikkeibank.repository.UserRepository;
import springboot_cntt2.it211_rikkeibank.service.FileStorageService;
import springboot_cntt2.it211_rikkeibank.service.RegisterService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final KycProfileRepository kycProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã tồn tại");
        }

        if (kycProfileRepository.existsByIdNumber(request.getIdNumber())) {
            throw new BadRequestException("Số CCCD đã tồn tại");
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("ROLE_CUSTOMER")
                        .description("Khách hàng")
                        .build()));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .isActive(true)
                .isKyc(false)
                .createdAt(LocalDateTime.now())
                .role(customerRole)
                .build();

        User savedUser = userRepository.save(user);

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .balance(BigDecimal.ZERO)
                .currency("VND")
                .transactionPin(passwordEncoder.encode(request.getTransactionPin()))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(savedUser)
                .build();

        Account savedAccount = accountRepository.save(account);

        String fileUrl = fileStorageService.saveFile(request.getIdCardFront());

        KycProfile kycProfile = KycProfile.builder()
                .idNumber(request.getIdNumber())
                .fullName(request.getFullName())
                .dob(request.getDob())
                .sex(request.getSex())
                .address(request.getAddress())
                .idCardFrontUrl(fileUrl)
                .status(KycStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .user(savedUser)
                .build();

        KycProfile savedKyc = kycProfileRepository.save(kycProfile);

        return RegisterResponse.builder()
                .user(UserResponse.from(savedUser))
                .account(AccountResponse.from(savedAccount))
                .kycProfile(KycResponse.from(savedKyc))
                .build();
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;

        do {
            accountNumber = "100" + String.format("%07d", random.nextInt(10_000_000));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}