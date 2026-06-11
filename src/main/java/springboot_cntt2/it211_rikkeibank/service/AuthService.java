package springboot_cntt2.it211_rikkeibank.service;


import springboot_cntt2.it211_rikkeibank.dto.request.*;
import springboot_cntt2.it211_rikkeibank.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String accessToken, LogoutRequest request);

    void changePin(ChangePinRequest request);

    void forgotPassword(ForgotPasswordRequest request);
}
