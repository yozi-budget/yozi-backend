package com.example.yozi.user.service;

import com.example.yozi.user.entity.User;
import com.example.yozi.user.entity.enums.SocialType;
import com.example.yozi.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final List<SocialOauth> socialOauthList; // 등록된 모든 SocialOauth 구현체 리스트
    private final UserRepository userRepository;
    private final JwtService jwtService; // JwtService 주입

    /**
     * 특정 소셜 타입에 해당하는 OAuth 리다이렉트 URL을 반환합니다.
     * @param socialType 소셜 타입 (KAKAO, GOOGLE 등)
     * @return 리다이렉트 URL
     */
    public String getOauthRedirectURL(SocialType socialType) {
        return findOauthByType(socialType).getOauthRedirectURL();
    }

    /**
     * 인가 코드를 사용하여 액세스 토큰을 요청하고 사용자 정보를 가져와 JWT를 생성합니다.
     * @param socialType 소셜 타입
     * @param code 인가 코드
     * @return 생성된 JWT 문자열
     */
    public String requestAccessToken(SocialType socialType, String code) {
        SocialOauth oauth = findOauthByType(socialType);
        String tokenJson = oauth.requestAccessToken(code); // 액세스 토큰 요청
        String accessToken = extractAccessToken(tokenJson); // JSON에서 access_token만 파싱
        String userInfoJson = oauth.requestUserInfo(accessToken); // 사용자 정보 요청

        User user = saveOrGetUser(socialType, userInfoJson); // 사용자 저장 또는 조회
        // 사용자 정보를 기반으로 JWT를 생성하여 반환
        return jwtService.generateToken(user);
    }

    /**
     * 소셜 서비스에서 가져온 사용자 정보를 기반으로 User 엔티티를 저장하거나 조회합니다.
     * @param socialType 소셜 타입
     * @param userInfoJson 사용자 정보 JSON 문자열
     * @return 저장되거나 조회된 User 엔티티
     */
    private User saveOrGetUser(SocialType socialType, String userInfoJson) {
        String socialId;
        String email;
        String nickname;

        if (socialType == SocialType.KAKAO) {
            JsonNode root = parse(userInfoJson);
            socialId = root.path("id").asText();
            email = root.path("kakao_account").path("email").asText(""); // 이메일 없을 경우 빈 문자열
            nickname = root.path("properties").path("nickname").asText("카카오 사용자"); // 닉네임 없을 경우 기본값
        } else { // Google
            JsonNode root = parse(userInfoJson);
            socialId = root.path("sub").asText(); // Google의 경우 'sub' 필드가 사용자 고유 ID
            email = root.path("email").asText();
            nickname = root.path("name").asText("구글 사용자");
        }

        // socialId와 socialType으로 기존 사용자 조회, 없으면 새로 생성하여 저장
        return userRepository.findBySocialIdAndSocialType(socialId, socialType)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(socialId)
                                .socialType(socialType)
                                .email(email)
                                .nickname(nickname)
                                .build()));
    }

    /**
     * JSON 문자열을 JsonNode 객체로 파싱합니다.
     * @param json JSON 문자열
     * @return 파싱된 JsonNode
     * @throws RuntimeException JSON 파싱 오류 시 발생
     */
    private JsonNode parse(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 오류", e);
        }
    }

    /**
     * 토큰 응답 JSON에서 access_token을 추출합니다.
     * @param tokenJson 토큰 응답 JSON 문자열
     * @return 추출된 access_token
     * @throws RuntimeException 액세스 토큰 추출 실패 시 발생
     */
    private String extractAccessToken(String tokenJson) {
        try {
            return new ObjectMapper().readTree(tokenJson).path("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("액세스 토큰 추출 실패", e);
        }
    }

    /**
     * 주어진 SocialType에 해당하는 SocialOauth 구현체를 찾아 반환합니다.
     * @param type 찾을 소셜 타입
     * @return 해당 SocialOauth 구현체
     * @throws IllegalArgumentException 지원하지 않는 소셜 로그인 타입일 경우 발생
     */
    private SocialOauth findOauthByType(SocialType type) {
        return socialOauthList.stream()
                .filter(oauth -> oauth.type() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 소셜 로그인: " + type));
    }
}