package springboot_cntt2.it211_rikkeibank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springboot_cntt2.it211_rikkeibank.dto.request.CreateAccountRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.AccountResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.BalanceResponse;
import springboot_cntt2.it211_rikkeibank.entity.Account;
import springboot_cntt2.it211_rikkeibank.entity.User;
import springboot_cntt2.it211_rikkeibank.exception.NotFoundException;
import springboot_cntt2.it211_rikkeibank.repository.AccountRepository;
import springboot_cntt2.it211_rikkeibank.repository.UserRepository;
import springboot_cntt2.it211_rikkeibank.service.AccountService;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user id: " + request.getUserId()));

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .balance(request.getBalance())
                .currency("VND")
                .transactionPin(passwordEncoder.encode(request.getTransactionPin()))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        return AccountResponse.from(accountRepository.save(account));
    }

    @Override
    public Page<AccountResponse> getAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return accountRepository.findAll(pageable)
                .map(AccountResponse::from);
    }

    @Override
    public AccountResponse getAccountById(Long id) {
        Account account = findAccountById(id);
        return AccountResponse.from(account);
    }

    @Override
    public BalanceResponse getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản: " + accountNumber));

        return BalanceResponse.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = findAccountById(id);
        account.setActive(false);
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy account id: " + id));
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