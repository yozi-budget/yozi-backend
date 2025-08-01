package com.example.yozi.user.service;

import com.example.yozi.user.entity.enums.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap; // Map 사용을 위해 임포트
import java.util.Map; // Map 사용을 위해 임포트
import java.util.stream.Collectors; // Collectors 사용을 위해 임포트

@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth {

    // 카카오 로그인 관련 설정 값 (application.properties 또는 application.yml에서 주입)
    @Value("${oauth.kakao.url}") // 변경된 키 이름
    private String kakaoBaseUrl;

    @Value("${oauth.kakao.client.id}") // 변경된 키 이름
    private String clientId;

    @Value("${oauth.kakao.callback.url}") // 변경된 키 이름
    private String callbackUrl;

    @Value("${oauth.kakao.token.url}") // 변경된 키 이름
    private String tokenUrl;

    @Value("${oauth.kakao.userinfo.url}") // 변경된 키 이름
    private String userInfoUrl;

    private final RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 위한 RestTemplate

    @Override
    public String getOauthRedirectURL() {
        // 카카오 인가 코드 요청 URL 생성
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", clientId);
        params.put("redirect_uri", callbackUrl);

        String query = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        return kakaoBaseUrl + "?" + query;
    }

    @Override
    public String requestAccessToken(String code) {
        // 액세스 토큰 요청을 위한 HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 액세스 토큰 요청을 위한 파라미터 설정 (MultiValueMap 대신 Map 사용)
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", clientId);
        params.put("redirect_uri", callbackUrl);
        params.put("code", code);

        // Map을 URL 인코딩된 문자열로 변환
        String body = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        // HTTP 엔티티 생성 (헤더 + 바디)
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // 카카오 토큰 발급 API 호출
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // JSON (access_token 포함)
        }

        throw new RuntimeException("카카오 액세스 토큰 요청 실패: " + response.getStatusCode());
    }

    @Override
    public String requestUserInfo(String accessToken) {
        // 사용자 정보 요청을 위한 HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Bearer 토큰 설정
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 카카오 API는 보통 application/x-www-form-urlencoded를 요구

        // HTTP 엔티티 생성 (바디 없음)
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 카카오 사용자 정보 API 호출
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // JSON (사용자 정보 포함)
        }

        throw new RuntimeException("카카오 사용자 정보 요청 실패: " + response.getStatusCode());
    }

    @Override
    public SocialType type() {
        return SocialType.KAKAO;
    }
}