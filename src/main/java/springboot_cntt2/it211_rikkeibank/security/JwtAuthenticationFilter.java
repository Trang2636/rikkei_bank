package springboot_cntt2.it211_rikkeibank.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import springboot_cntt2.it211_rikkeibank.service.TokenBlacklistService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Service dùng Redis để kiểm tra token đã logout chưa
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Lấy header Authorization từ request
        String authHeader = request.getHeader("Authorization");

        /*
         * Nếu request không có token thì bỏ qua filter.
         * Những API public như login/register vẫn chạy bình thường.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Cắt bỏ chữ "Bearer " để lấy token thật
        String token = authHeader.substring(7);

        /*
         * Kiểm tra token có nằm trong Redis blacklist không.
         * Nếu có nghĩa là user đã logout rồi.
         */
        if (tokenBlacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Token đã bị logout/blacklist trong Redis"
                    }
                    """);
            return;
        }

        try {
            // Lấy username từ JWT
            String username = jwtService.extractUsername(token);

            /*
             * Nếu token có username và hiện tại SecurityContext chưa có authentication
             * thì tiến hành xác thực token.
             */
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load thông tin user từ database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Kiểm tra token có hợp lệ và chưa hết hạn không
                if (jwtService.isTokenValid(token, userDetails)) {

                    /*
                     * Tạo authentication để Spring Security hiểu rằng
                     * user này đã đăng nhập thành công.
                     */
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Gắn thêm thông tin request hiện tại
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Lưu authentication vào SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            /*
             * Nếu token sai, hết hạn, bị sửa nội dung hoặc không parse được
             * thì trả về 401.
             */
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Access Token không hợp lệ hoặc đã hết hạn"
                    }
                    """);
            return;
        }

        // Cho request đi tiếp vào controller
        filterChain.doFilter(request, response);
    }
}