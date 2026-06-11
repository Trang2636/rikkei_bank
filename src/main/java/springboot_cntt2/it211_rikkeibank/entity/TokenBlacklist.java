package springboot_cntt2.it211_rikkeibank.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String accessToken;

    private LocalDateTime expiryAt;

    private LocalDateTime blacklistedAt;

    private LocalDateTime createdAt;
}
