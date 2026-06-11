package springboot_cntt2.it211_rikkeibank.dto.request;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutRequest {

    private String refreshToken;
}
