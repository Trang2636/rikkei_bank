package springboot_cntt2.it211_rikkeibank.service.impl;
import springboot_cntt2.it211_rikkeibank.service.TokenBlacklistService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot_cntt2.it211_rikkeibank.dto.request.ChangePinRequest;
import springboot_cntt2.it211_rikkeibank.dto.request.ForgotPasswordRequest;
import springboot_cntt2.it211_rikkeibank.dto.request.LoginRequest;
import springboot_cntt2.it211_rikkeibank.dto.request.LogoutRequest;
import springboot_cntt2.it211_rikkeibank.dto.request.RefreshTokenRequest;
import springboot_cntt2.it211_rikkeibank.dto.response.AuthResponse;
import springboot_cntt2.it211_rikkeibank.dto.response.UserResponse;
import springboot_cntt2.it211_rikkeibank.entity.Account;
import springboot_cntt2.it211_rikkeibank.entity.RefreshToken;
import springboot_cntt2.it211_rikkeibank.entity.TokenBlacklist;
import springboot_cntt2.it211_rikkeibank.entity.User;
import springboot_cntt2.it211_rikkeibank.exception.BadRequestException;
import springboot_cntt2.it211_rikkeibank.exception.ForbiddenException;
import springboot_cntt2.it211_rikkeibank.exception.NotFoundException;
import springboot_cntt2.it211_rikkeibank.repository.AccountRepository;
import springboot_cntt2.it211_rikkeibank.repository.RefreshTokenRepository;
import springboot_cntt2.it211_rikkeibank.repository.TokenBlacklistRepository;
import springboot_cntt2.it211_rikkeibank.repository.UserRepository;
import springboot_cntt2.it211_rikkeibank.security.JwtService;
import springboot_cntt2.it211_rikkeibank.service.AuthService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.time.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenBlacklistService tokenBlacklistService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ForbiddenException("Tài khoản đã bị khóa");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(UserResponse.from(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken oldRefreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Refresh token không tồn tại"));

        if (Boolean.TRUE.equals(oldRefreshToken.getRevoked())) {
            throw new BadRequestException("Refresh token đã bị thu hồi");
        }

        if (oldRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token đã hết hạn");
        }

        String username = jwtService.extractUsername(request.getRefreshToken());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new BadRequestException("Refresh token không hợp lệ");
        }

        oldRefreshToken.setRevoked(true);
        refreshTokenRepository.save(oldRefreshToken);

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        refreshTokenRepository.save(RefreshToken.builder()
                .token(newRefreshToken)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .user(UserResponse.from(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String accessToken, LogoutRequest request) {

        /*
         * accessToken lấy từ header:
         * Authorization: Bearer <token>
         */
        if (accessToken != null && accessToken.startsWith("Bearer ")) {

            // Cắt bỏ chữ "Bearer " để lấy token thật
            String token = accessToken.substring(7);

            // Lấy thời gian hết hạn của access token
            Date expiryDate = jwtService.extractExpiration(token);

            /*
             * Tính thời gian sống còn lại của token.
             * Ví dụ token còn 3 phút thì Redis chỉ lưu blacklist trong 3 phút.
             */
            long ttlMillis = expiryDate.getTime() - System.currentTimeMillis();

            /*
             * Nếu token vẫn còn hạn thì mới cần đưa vào blacklist.
             * Nếu token hết hạn rồi thì không cần lưu nữa.
             */
            if (ttlMillis > 0) {
                tokenBlacklistService.blacklist(token, Duration.ofMillis(ttlMillis));
            }
        }

        /*
         * Refresh token cũng bị revoke để user không thể dùng nó
         * xin access token mới sau khi logout.
         */
        if (request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            refreshTokenRepository.findByToken(request.getRefreshToken())
                    .ifPresent(refreshToken -> {
                        refreshToken.setRevoked(true);
                        refreshTokenRepository.save(refreshToken);
                    });
        }
    }

    @Override
    @Transactional
    public void changePin(ChangePinRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy user"));

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Bạn không phải chủ tài khoản này");
        }

        if (!passwordEncoder.matches(request.getOldPin(), account.getTransactionPin())) {
            throw new BadRequestException("PIN cũ không đúng");
        }

        account.setTransactionPin(passwordEncoder.encode(request.getNewPin()));
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy email"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
