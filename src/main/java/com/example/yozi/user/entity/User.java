package com.example.yozi.user.entity;

import com.example.yozi.user.entity.enums.SocialType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime; // LocalDateTime 임포트

@Entity
@Getter
@Setter // Setter 추가 (기존 코드에 이미 있었음)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 접근 제어자 PROTECTED로 유지
@AllArgsConstructor
@Builder
@Table(name = "users") // 테이블 이름 지정 (user는 예약어일 수 있음)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // 컬럼 이름 명시
    private Long id; // 사용자 고유 ID

    @Column(name = "social_id", nullable = false, unique = true) // 컬럼 이름 명시
    private String socialId; // 소셜 로그인 서비스에서 제공하는 사용자 ID

    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    @Column(name = "social_type", nullable = false) // 컬럼 이름 명시
    private SocialType socialType; // 소셜 로그인 타입 (KAKAO, GOOGLE 등)

    @Column(name = "nickname", nullable = false) // 컬럼 이름 명시
    private String nickname; // 사용자 닉네임

    @Column(name = "email") // 컬럼 이름 명시
    private String email; // 사용자 이메일

    @Column(name = "created_at", nullable = false, updatable = false) // 생성 시간, 업데이트 불가
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false) // 업데이트 시간
    private LocalDateTime updatedAt;

    // 엔티티가 영속화되기 전에 호출 (생성 시간 설정)
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt; // 생성 시 업데이트 시간도 동일하게 설정
    }

    // 엔티티가 업데이트되기 전에 호출 (업데이트 시간 설정)
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}