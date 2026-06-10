package springboot_cntt2.it211_rikkeibank.entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_cntt2.it211_rikkeibank.enums.KycStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Số CCCD/Passport
    @Column(nullable = false, unique = true)
    private String idNumber;

    private String fullName;

    private LocalDate dob;

    private String sex;

    private String address;
    private String idCardFrontUrl;

    @Enumerated(EnumType.STRING)
    private KycStatus status;

    private LocalDateTime verifiedAt;

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
