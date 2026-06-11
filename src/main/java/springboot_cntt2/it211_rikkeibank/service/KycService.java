package springboot_cntt2.it211_rikkeibank.service;

import springboot_cntt2.it211_rikkeibank.dto.request.KycApproveRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.KycResponse;

public interface KycService {
    KycResponse approveKyc(Long kycId, KycApproveRequest request);
}
