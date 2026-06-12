package springboot_cntt2.it211_rikkeibank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "function_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FunctionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên class được gọi, ví dụ: AccountServiceImpl
    private String className;

    // Tên method được gọi, ví dụ: getBalance
    private String methodName;

    // Thời gian thực thi method, tính bằng mili giây
    private Long executionTimeMs;

    // Trạng thái thực thi: SUCCESS hoặc FAILED
    private String status;

    // Nếu method lỗi thì lưu message lỗi ở đây
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    // Thời điểm ghi log
    private LocalDateTime createdAt;
}