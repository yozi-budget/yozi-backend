// package com.example.yozi.jwt;

package com.example.yozi.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Collections;
import java.util.Enumeration; // Enumeration 임포트
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("--- JWT Filter: Processing request for URI: {} ---", request.getRequestURI());

        // 모든 요청 헤더를 로깅합니다. (디버깅 목적)
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("Header: {} = {}", headerName, request.getHeader(headerName));
        }
        log.info("--- End of Headers ---");

        String token = resolveToken(request); // 요청 헤더에서 JWT 토큰 추출
        log.info("JWT Filter: Extracted token (first 20 chars): {}", (token != null && token.length() > 20) ? token.substring(0, 20) + "..." : token);

        if (token != null) {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                Long userId = claims.get("userId", Long.class);
                String socialId = claims.get("socialId", String.class);
                String nickname = claims.get("nickname", String.class);

                UserPrincipal userPrincipal = new UserPrincipal(
                        userId,
                        socialId,
                        nickname,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );

                Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT Filter: Authentication successful for user ID: {}", userId);

            } catch (SecurityException | MalformedJwtException e) {
                log.warn("잘못된 JWT 서명입니다: {}", e.getMessage());
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
            } catch (ExpiredJwtException e) {
                log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
            } catch (UnsupportedJwtException e) {
                log.warn("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported JWT token");
            } catch (IllegalArgumentException e) {
                log.warn("JWT 토큰이 잘못되었거나 비어있습니다: {}", e.getMessage());
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is invalid or empty");
            } catch (Exception e) {
                log.error("JWT 필터 처리 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
                // response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error during JWT processing");
            }
        } else {
            log.info("JWT Filter: No token found in Authorization header.");
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
