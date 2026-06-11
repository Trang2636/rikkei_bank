package springboot_cntt2.it211_rikkeibank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_cntt2.it211_rikkeibank.dto.request.TransferRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.TransactionResponse;
import springboot_cntt2.it211_rikkeibank.entity.Account;
import springboot_cntt2.it211_rikkeibank.entity.Transaction;
import springboot_cntt2.it211_rikkeibank.entity.User;
import springboot_cntt2.it211_rikkeibank.enums.TransactionStatus;
import springboot_cntt2.it211_rikkeibank.exception.BadRequestException;
import springboot_cntt2.it211_rikkeibank.exception.ForbiddenException;
import springboot_cntt2.it211_rikkeibank.exception.InsufficientBalanceException;
import springboot_cntt2.it211_rikkeibank.exception.NotFoundException;
import springboot_cntt2.it211_rikkeibank.repository.AccountRepository;
import springboot_cntt2.it211_rikkeibank.repository.TransactionRepository;
import springboot_cntt2.it211_rikkeibank.repository.UserRepository;
import springboot_cntt2.it211_rikkeibank.service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new BadRequestException("Không thể chuyển tiền cho chính tài khoản nguồn");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));

        if (!Boolean.TRUE.equals(currentUser.getIsKyc())) {
            throw new ForbiddenException("Bạn cần được duyệt eKYC trước khi chuyển tiền");
        }

        Account fromAccount = accountRepository.findByAccountNumberForUpdate(request.getFromAccountNumber())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản nguồn"));

        Account toAccount = accountRepository.findByAccountNumberForUpdate(request.getToAccountNumber())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản nhận"));

        if (!fromAccount.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không phải chủ tài khoản nguồn");
        }

        if (!Boolean.TRUE.equals(fromAccount.getActive())) {
            throw new BadRequestException("Tài khoản nguồn đang bị khóa");
        }

        if (!Boolean.TRUE.equals(toAccount.getActive())) {
            throw new BadRequestException("Tài khoản nhận đang bị khóa");
        }

        if (!passwordEncoder.matches(request.getPin(), fromAccount.getTransactionPin())) {
            throw new BadRequestException("Mã PIN không chính xác");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Số dư không đủ để chuyển tiền");
        }

        BigDecimal newFromBalance = fromAccount.getBalance().subtract(request.getAmount());
        BigDecimal newToBalance = toAccount.getBalance().add(request.getAmount());

        fromAccount.setBalance(newFromBalance);
        fromAccount.setUpdatedAt(LocalDateTime.now());

        toAccount.setBalance(newToBalance);
        toAccount.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = Transaction.builder()
                .transactionCode(generateTransactionCode())
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(request.getAmount())
                .description(request.getDescription())
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return TransactionResponse.from(savedTransaction, request.getFromAccountNumber());
    }

    @Override
    public Page<TransactionResponse> getStatement(String accountNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return transactionRepository.findStatementByAccountNumber(accountNumber, pageable)
                .map(transaction -> TransactionResponse.from(transaction, accountNumber));
    }

    private String generateTransactionCode() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
