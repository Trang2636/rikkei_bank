package springboot_cntt2.it211_rikkeibank.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import springboot_cntt2.it211_rikkeibank.dto.request.CreateAccountRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.AccountResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.ApiResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.BalanceResponse;
import springboot_cntt2.it211_rikkeibank.service.AccountService;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo tài khoản ngân hàng thành công", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AccountResponse>>> getAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<AccountResponse> response = accountService.getAccounts(page, size);

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tài khoản thành công", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable Long id) {
        AccountResponse response = accountService.getAccountById(id);

        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết tài khoản thành công", response));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(@PathVariable String accountNumber) {
        BalanceResponse response = accountService.getBalance(accountNumber);

        return ResponseEntity.ok(ApiResponse.success("Vấn tin số dư thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);

        return ResponseEntity.ok(ApiResponse.success("Khóa tài khoản ngân hàng thành công", null));
    }
}