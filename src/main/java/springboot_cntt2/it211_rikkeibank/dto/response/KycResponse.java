package springboot_cntt2.it211_rikkeibank.dto.response;
import lombok.*;
import springboot_cntt2.it211_rikkeibank.entity.KycProfile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycResponse {

    private Long id;
    private String idNumber;
    private String fullName;
    private LocalDate dob;
    private String sex;
    private String address;
    private String idCardFrontUrl;
    private String status;
    private LocalDateTime createdAt;

    public static KycResponse from(KycProfile kycProfile) {
        return KycResponse.builder()
                .id(kycProfile.getId())
                .idNumber(kycProfile.getIdNumber())
                .fullName(kycProfile.getFullName())
                .dob(kycProfile.getDob())
                .sex(kycProfile.getSex())
                .address(kycProfile.getAddress())
                .idCardFrontUrl(kycProfile.getIdCardFrontUrl())
                .status(kycProfile.getStatus().name())
                .createdAt(kycProfile.getCreatedAt())
                .build();
    }
}