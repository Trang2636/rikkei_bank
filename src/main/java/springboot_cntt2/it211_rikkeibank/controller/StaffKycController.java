package springboot_cntt2.it211_rikkeibank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import springboot_cntt2.it211_rikkeibank.dto.request.KycApproveRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.ApiResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.KycResponse;
import springboot_cntt2.it211_rikkeibank.service.KycService;

@RestController
@RequestMapping("/api/v1/staff/kyc")
@RequiredArgsConstructor
public class StaffKycController {

    private final KycService kycService;

    @PutMapping("/{kycId}/approve")
    public ResponseEntity<ApiResponse<KycResponse>> approveKyc(
            @PathVariable Long kycId,
            @Valid @RequestBody KycApproveRequest request
    ) {
        KycResponse response = kycService.approveKyc(kycId, request);

        return ResponseEntity.ok(ApiResponse.success("Duyệt hồ sơ eKYC thành công", response));
    }
}
