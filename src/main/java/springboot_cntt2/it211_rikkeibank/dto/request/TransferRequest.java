package springboot_cntt2.it211_rikkeibank.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    @NotBlank(message = "Tài khoản nguồn không được để trống")
    private String fromAccountNumber;

    @NotBlank(message = "Tài khoản nhận không được để trống")
    private String toAccountNumber;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "1000.0", message = "Số tiền chuyển tối thiểu là 1000")
    private BigDecimal amount;

    private String description;

    @NotBlank(message = "Mã PIN không được để trống")
    private String pin;
}