package springboot_cntt2.it211_rikkeibank.service;

import org.springframework.data.domain.Page;
import springboot_cntt2.it211_rikkeibank.dto.request.CreateAccountRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.AccountResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.BalanceResponse;

public interface AccountService {
    AccountResponse createAccount(CreateAccountRequest request);

    Page<AccountResponse> getAccounts(int page, int size);

    AccountResponse getAccountById(Long id);

    BalanceResponse getBalance(String accountNumber);

    void deleteAccount(Long id);
}
