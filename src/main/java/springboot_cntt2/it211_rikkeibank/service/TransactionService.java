package springboot_cntt2.it211_rikkeibank.service;

import org.springframework.data.domain.Page;
import springboot_cntt2.it211_rikkeibank.dto.request.TransferRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.TransactionResponse;

public interface TransactionService {

    TransactionResponse transfer(TransferRequest request);

    Page<TransactionResponse> getStatement(String accountNumber, int page, int size);
}