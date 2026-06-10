package springboot_cntt2.it211_rikkeibank.service;

import org.springframework.data.domain.Page;
import springboot_cntt2.it211_rikkeibank.dto.request.CreateUserRequest;
import springboot_cntt2.it211_rikkeibank.dto.request.UpdateUserRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    Page<UserResponse> getUsers(int page, int size);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}
