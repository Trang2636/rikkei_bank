package springboot_cntt2.it211_rikkeibank.dto.response;

import lombok.*;
import springboot_cntt2.it211_rikkeibank.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private String transactionCode;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String description;
    private String status;
    private String type;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction transaction, String currentAccountNumber) {
        String type = null;

        if (currentAccountNumber != null) {
            if (transaction.getFromAccount().getAccountNumber().equals(currentAccountNumber)) {
                type = "DEBIT";
            } else if (transaction.getToAccount().getAccountNumber().equals(currentAccountNumber)) {
                type = "CREDIT";
            }
        }

        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionCode(transaction.getTransactionCode())
                .fromAccountNumber(transaction.getFromAccount().getAccountNumber())
                .toAccountNumber(transaction.getToAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .status(transaction.getStatus().name())
                .type(type)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}