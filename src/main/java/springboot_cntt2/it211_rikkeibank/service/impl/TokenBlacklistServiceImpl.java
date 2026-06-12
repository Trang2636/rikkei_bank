package springboot_cntt2.it211_rikkeibank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import springboot_cntt2.it211_rikkeibank.service.TokenBlacklistService;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    /*
     * StringRedisTemplate dùng để thao tác Redis với key/value dạng String.
     * Ở đây mình lưu token đã logout vào Redis.
     */
    private final StringRedisTemplate stringRedisTemplate;

    // Prefix giúp key trong Redis dễ nhận biết
    private static final String PREFIX = "blacklist:access-token:";

    @Override
    public void blacklist(String token, Duration ttl) {
        /*
         * Lưu token vào Redis.
         *
         * Key: blacklist:access-token:<token>
         * Value: blacklisted
         * ttl: thời gian sống còn lại của token
         *
         * Khi token hết hạn thật, Redis tự xóa key này.
         */
        stringRedisTemplate.opsForValue().set(PREFIX + token, "blacklisted", ttl);
    }

    @Override
    public boolean isBlacklisted(String token) {
        // Nếu Redis có key này nghĩa là token đã bị logout
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + token));
    }
}