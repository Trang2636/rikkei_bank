package springboot_cntt2.it211_rikkeibank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePinRequest {

    @NotBlank(message = "Số tài khoản không được để trống")
    private String accountNumber;

    @NotBlank(message = "PIN cũ không được để trống")
    private String oldPin;

    @NotBlank(message = "PIN mới không được để trống")
    @Size(min = 6, max = 6, message = "PIN mới phải gồm 6 ký tự")
    private String newPin;
}
