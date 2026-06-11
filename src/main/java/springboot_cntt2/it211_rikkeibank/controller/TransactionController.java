package springboot_cntt2.it211_rikkeibank.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import springboot_cntt2.it211_rikkeibank.dto.request.TransferRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.ApiResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.TransactionResponse;
import springboot_cntt2.it211_rikkeibank.service.TransactionService;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request);

        return ResponseEntity.ok(ApiResponse.success("Chuyển tiền thành công", response));
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getStatement(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<TransactionResponse> response = transactionService.getStatement(accountNumber, page, size);

        return ResponseEntity.ok(ApiResponse.success("Lấy sao kê thành công", response));
    }
}