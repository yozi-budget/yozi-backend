package com.example.yozi.user.service;

import com.example.yozi.user.entity.enums.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap; // LinkedMultiValueMap 사용을 위해 임포트
import org.springframework.util.MultiValueMap; // MultiValueMap 사용을 위해 임포트
import org.springframework.web.client.RestTemplate;

import java.util.HashMap; // Map 사용을 위해 임포트
import java.util.Map; // Map 사용을 위해 임포트
import java.util.stream.Collectors; // Collectors 사용을 위해 임포트

@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {

    // 구글 로그인 관련 설정 값 (application.properties 또는 application.yml에서 주입)
    @Value("${oauth.google.url}") // 변경된 키 이름
    private String googleBaseUrl;

    @Value("${oauth.google.client.id}") // 변경된 키 이름
    private String clientId;

    @Value("${oauth.google.client.secret}") // 변경된 키 이름
    private String clientSecret;

    @Value("${oauth.google.callback.url}") // 변경된 키 이름
    private String redirectUrl;

    @Value("${oauth.google.token.url}") // 변경된 키 이름
    private String tokenUrl;

    @Value("${oauth.google.userinfo.url}") // 변경된 키 이름
    private String userInfoUrl;

    private final RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 위한 RestTemplate

    @Override
    public String getOauthRedirectURL() {
        // 구글 인가 코드 요청 URL 생성
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", clientId);
        params.put("redirect_uri", redirectUrl);
        params.put("response_type", "code");
        params.put("scope", "openid email profile"); // 스코프는 공백으로 구분

        String query = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        return googleBaseUrl + "?" + query;
    }

    @Override
    public String requestAccessToken(String code) {
        // 액세스 토큰 요청을 위한 HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 액세스 토큰 요청을 위한 파라미터 설정 (MultiValueMap 사용)
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUrl);
        params.add("code", code);

        // HTTP 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 구글 토큰 발급 API 호출
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // JSON (access_token 포함)
        }

        throw new RuntimeException("구글 액세스 토큰 요청 실패: " + response.getStatusCode());
    }

    @Override
    public String requestUserInfo(String accessToken) {
        // 사용자 정보 요청을 위한 HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Bearer 토큰 설정
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // 구글 API는 보통 application/x-www-form-urlencoded를 요구

        // HTTP 엔티티 생성 (바디 없음)
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 구글 사용자 정보 API 호출
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // JSON (사용자 정보 포함)
        }

        throw new RuntimeException("구글 사용자 정보 요청 실패: " + response.getStatusCode());
    }

    @Override
    public SocialType type() {
        return SocialType.GOOGLE;
    }
}