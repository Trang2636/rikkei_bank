package springboot_cntt2.it211_rikkeibank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import springboot_cntt2.it211_rikkeibank.dto.request.*;
import springboot_cntt2.it211_rikkeibank.dto.response.ApiResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.AuthResponse;
import springboot_cntt2.it211_rikkeibank.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);

        return ResponseEntity.ok(ApiResponse.success("Refresh token thành công", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            @RequestBody LogoutRequest request
    ) {
        authService.logout(accessToken, request);

        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }

    @PutMapping("/change-pin")
    public ResponseEntity<ApiResponse<Void>> changePin(@Valid @RequestBody ChangePinRequest request) {
        authService.changePin(request);

        return ResponseEntity.ok(ApiResponse.success("Đổi mã PIN thành công", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);

        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }
}