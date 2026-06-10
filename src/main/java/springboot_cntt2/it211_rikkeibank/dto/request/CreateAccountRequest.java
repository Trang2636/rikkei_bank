package springboot_cntt2.it211_rikkeibank.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotNull(message = "User id không được để trống")
    private Long userId;

    @NotNull(message = "Số dư ban đầu không được để trống")
    @DecimalMin(value = "0.0", message = "Số dư không được âm")
    private BigDecimal balance;

    @NotBlank(message = "Mã PIN không được để trống")
    @Size(min = 6, max = 6, message = "Mã PIN phải gồm 6 ký tự")
    private String transactionPin;
}
