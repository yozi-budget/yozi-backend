package com.example.yozi.user.service;

import com.example.yozi.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
// Claims 임포트 추가 (필요 시)
// import io.jsonwebtoken.Claims;


@Service
public class JwtService {

    // JWT 서명에 사용할 비밀 키 (application.properties 또는 application.yml에서 주입받아 사용)
    @Value("${jwt.secret}")
    private String secretKey;

    // JWT 만료 시간 (밀리초 단위, 예: 1시간)
    @Value("${jwt.expiration}")
    private long expirationTime; // 3600000L (1시간)

    // 비밀 키를 디코딩하여 Key 객체로 반환
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 사용자 정보를 기반으로 JWT를 생성합니다.
     * @param user JWT에 포함될 사용자 정보
     * @return 생성된 JWT 문자열
     */
    public String generateToken(User user) {
        // 현재 시간
        Date now = new Date();
        // 만료 시간 설정
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .claim("userId", user.getId()) // 사용자 고유 ID
                .claim("socialId", user.getSocialId()) // 소셜 ID
                .claim("socialType", user.getSocialType().name()) // 소셜 타입 (Enum 이름을 문자열로)
                .claim("nickname", user.getNickname()) // 닉네임
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명에 사용할 키와 알고리즘
                .compact(); // JWT 생성
    }

    // TODO: 필요하다면 JWT 유효성 검사 및 정보 추출 메서드 추가
    // public Claims extractAllClaims(String token) { ... }
    // public Boolean validateToken(String token, UserDetails userDetails) { ... }
}