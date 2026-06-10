package springboot_cntt2.it211_rikkeibank.dto.response;


import lombok.*;
import springboot_cntt2.it211_rikkeibank.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
    private Boolean isKyc;
    private String roleName;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .isActive(user.getIsActive())
                .isKyc(user.getIsKyc())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}