package springboot_cntt2.it211_rikkeibank.service;

import java.time.Duration;

public interface TokenBlacklistService {

    // Lưu access token vào Redis blacklist
    void blacklist(String token, Duration ttl);

    // Kiểm tra access token có nằm trong Redis blacklist không
    boolean isBlacklisted(String token);
}
