package com.example.yozi.user.controller;

import com.example.yozi.user.entity.enums.SocialType;
import com.example.yozi.user.service.OauthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class OauthController {

    private final OauthService oauthService;

    @Operation(summary = "OAuth 로그인 리다이렉트", description = "소셜 타입에 따른 OAuth 로그인 페이지로 리다이렉트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "OAuth 로그인 페이지로 리다이렉트")
    })
    @GetMapping("/{socialType}")
    public RedirectView redirectToLogin(
            @Parameter(description = "소셜 로그인 타입 (예: GOOGLE, KAKAO 등)", required = true)
            @PathVariable SocialType socialType) {
        String redirectUrl = oauthService.getOauthRedirectURL(socialType);
        log.info("Redirecting to OAuth URL for {}: {}", socialType, redirectUrl);
        return new RedirectView(redirectUrl);
    }

    @Operation(summary = "OAuth 콜백 처리", description = "OAuth 서버로부터 받은 코드로 JWT 토큰을 발급받아 프론트엔드로 리다이렉트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "302", description = "프론트엔드 로그인 성공 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "302", description = "에러 발생 시 프론트엔드 에러 페이지로 리다이렉트")
    })
    @GetMapping("/{socialType}/callback")
    public RedirectView oauthCallback(
            @Parameter(description = "소셜 로그인 타입 (예: GOOGLE, KAKAO 등)", required = true)
            @PathVariable SocialType socialType,
            @Parameter(description = "OAuth 서버에서 받은 인가 코드", required = true)
            @RequestParam String code) {
        log.info("Received callback for {} with code: {}", socialType, code);
        String jwtToken;
        try {
            jwtToken = oauthService.requestAccessToken(socialType, code);
            log.info("Successfully obtained JWT for {}: {}", socialType, jwtToken);
        } catch (Exception e) {
            log.error("Failed to obtain JWT for {}: {}", socialType, e.getMessage());
            return new RedirectView("https://yozi-frontend.vercel.app/auth/error?message=" + e.getMessage());
        }

        String frontendRedirectUrl = UriComponentsBuilder.fromUriString("https://yozi-frontend.vercel.app/auth/success")
                .queryParam("token", jwtToken)
                .build()
                .toUriString();

        log.info("Redirecting to frontend with JWT: {}", frontendRedirectUrl);
        return new RedirectView(frontendRedirectUrl);
    }
/*
    @Operation(summary = "JWT 인증 테스트용 엔드포인트", description = "JWT 인증이 필요한 API의 접근 테스트용 엔드포인트입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공 메시지 반환")
    })
    @GetMapping("/protected-test")
    public ResponseEntity<String> testProtectedEndpoint() {
        return ResponseEntity.ok("이것은 JWT로 보호된 엔드포인트입니다! 인증 성공.");
    }

 */
}
