package springboot_cntt2.it211_rikkeibank.service;

import springboot_cntt2.it211_rikkeibank.dto.request.RegisterRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.RegisterResponse;

public interface RegisterService {
    RegisterResponse register(RegisterRequest request);
}
