package springboot_cntt2.it211_rikkeibank.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import springboot_cntt2.it211_rikkeibank.dto.request.CreateUserRequest;
import springboot_cntt2.it211_rikkeibank.dto.request.UpdateUserRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.ApiResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.UserResponse;
import springboot_cntt2.it211_rikkeibank.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo user thành công", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<UserResponse> response = userService.getUsers(page, size);

        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách user thành công", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);

        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết user thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse response = userService.updateUser(id, request);

        return ResponseEntity.ok(ApiResponse.success("Cập nhật user thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(ApiResponse.success("Khóa user thành công", null));
    }
}
