package springboot_cntt2.it211_rikkeibank.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import springboot_cntt2.it211_rikkeibank.enums.KycStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycApproveRequest {

    @NotNull(message = "Trạng thái duyệt không được để trống")
    private KycStatus status;
}
