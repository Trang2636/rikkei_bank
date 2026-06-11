package springboot_cntt2.it211_rikkeibank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_cntt2.it211_rikkeibank.dto.request.KycApproveRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.KycResponse;
import springboot_cntt2.it211_rikkeibank.entity.KycProfile;
import springboot_cntt2.it211_rikkeibank.enums.KycStatus;
import springboot_cntt2.it211_rikkeibank.exception.BadRequestException;
import springboot_cntt2.it211_rikkeibank.exception.NotFoundException;
import springboot_cntt2.it211_rikkeibank.repository.KycProfileRepository;
import springboot_cntt2.it211_rikkeibank.repository.UserRepository;
import springboot_cntt2.it211_rikkeibank.service.KycService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {

    private final KycProfileRepository kycProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public KycResponse approveKyc(Long kycId, KycApproveRequest request) {
        KycProfile kycProfile = kycProfileRepository.findById(kycId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ eKYC"));

        if (kycProfile.getStatus() != KycStatus.PENDING) {
            throw new BadRequestException("Hồ sơ eKYC này đã được xử lý");
        }

        if (request.getStatus() != KycStatus.CONFIRM && request.getStatus() != KycStatus.REJECT) {
            throw new BadRequestException("Trạng thái duyệt chỉ được là CONFIRM hoặc REJECT");
        }

        kycProfile.setStatus(request.getStatus());
        kycProfile.setVerifiedAt(LocalDateTime.now());

        if (request.getStatus() == KycStatus.CONFIRM) {
            kycProfile.getUser().setIsKyc(true);
            userRepository.save(kycProfile.getUser());
        }

        return KycResponse.from(kycProfileRepository.save(kycProfile));
    }
}
