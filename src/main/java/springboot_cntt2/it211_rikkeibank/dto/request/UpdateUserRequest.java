package springboot_cntt2.it211_rikkeibank.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    private String phoneNumber;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private Boolean isActive;
}
