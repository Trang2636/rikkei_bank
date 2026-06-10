package springboot_cntt2.it211_rikkeibank.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {

    private UserResponse user;
    private AccountResponse account;
    private KycResponse kycProfile;
}