package com.example.yozi.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

// UserDetails 인터페이스를 구현하여 Spring Security에서 사용자 정보를 관리할 수 있도록 합니다.
public class UserPrincipal implements UserDetails {

    private Long id; // 사용자 고유 ID (DB의 PK)
    private String socialId; // 소셜 서비스에서 제공하는 ID
    private String nickname; // 사용자 닉네임
    private Collection<? extends GrantedAuthority> authorities; // 사용자 권한

    public UserPrincipal(Long id, String socialId, String nickname, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.socialId = socialId;
        this.nickname = nickname;
        this.authorities = authorities;
    }

    // Getter 메서드
    public Long getId() {
        return id;
    }

    public String getSocialId() {
        return socialId;
    }

    public String getNickname() {
        return nickname;
    }

    // UserDetails 인터페이스 구현 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // JWT 기반 인증이므로 비밀번호는 필요 없음
    }

    @Override
    public String getUsername() {
        return socialId; // UserDetails 인터페이스의 username으로 socialId 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (항상 true로 설정)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (항상 true로 설정)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호) 만료 여부 (항상 true로 설정)
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부 (항상 true로 설정)
    }
}
