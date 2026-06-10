package springboot_cntt2.it211_rikkeibank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import springboot_cntt2.it211_rikkeibank.dto.request.RegisterRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.ApiResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.RegisterResponse;
import springboot_cntt2.it211_rikkeibank.service.RegisterService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @ModelAttribute RegisterRequest request) {
        RegisterResponse response = registerService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký mở tài khoản thành công, hồ sơ eKYC đang chờ duyệt", response));
    }
}
