package springboot_cntt2.it211_rikkeibank.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import springboot_cntt2.it211_rikkeibank.entity.FunctionLog;
import springboot_cntt2.it211_rikkeibank.repository.FunctionLogRepository;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionTimeLoggingAspect {

    private final FunctionLogRepository functionLogRepository;

    /*
     * @Around dùng để bao quanh method.
     * Nó chạy trước method, sau method và cả khi method bị lỗi.
     *
     * Pointcut này áp dụng cho:
     * - Tất cả method trong service.impl
     * - Tất cả method trong controller
     */
    @Around("execution(* springboot_cntt2.it211_rikkeibank.service.impl..*(..)) || execution(* springboot_cntt2.it211_rikkeibank.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        // Lấy thời điểm bắt đầu chạy method
        long start = System.currentTimeMillis();

        // Lấy tên class đang chạy
        String className = joinPoint.getSignature().getDeclaringTypeName();

        // Lấy tên method đang chạy
        String methodName = joinPoint.getSignature().getName();

        try {
            /*
             * joinPoint.proceed() nghĩa là cho method thật chạy.
             * Nếu không gọi dòng này thì controller/service sẽ không chạy.
             */
            Object result = joinPoint.proceed();

            // Tính thời gian chạy method
            long executionTime = System.currentTimeMillis() - start;

            // Lưu log thành công vào database
            functionLogRepository.save(FunctionLog.builder()
                    .className(className)
                    .methodName(methodName)
                    .executionTimeMs(executionTime)
                    .status("SUCCESS")
                    .createdAt(LocalDateTime.now())
                    .build());

            // In log ra console
            log.info("[AOP] {}.{} chạy trong {} ms - SUCCESS", className, methodName, executionTime);

            // Trả kết quả thật của method về cho client
            return result;

        } catch (Throwable e) {
            // Nếu method bị lỗi thì vẫn tính thời gian chạy
            long executionTime = System.currentTimeMillis() - start;

            // Lưu log thất bại vào database
            functionLogRepository.save(FunctionLog.builder()
                    .className(className)
                    .methodName(methodName)
                    .executionTimeMs(executionTime)
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build());

            // In lỗi ra console
            log.error("[AOP] {}.{} chạy trong {} ms - FAILED: {}",
                    className,
                    methodName,
                    executionTime,
                    e.getMessage()
            );

            // Ném lại lỗi để GlobalExceptionHandler xử lý tiếp
            throw e;
        }
    }
}