package com.example.yozi.user.service;

import com.example.yozi.user.entity.enums.SocialType;

public interface SocialOauth {
    String getOauthRedirectURL();
    String requestAccessToken(String code);
    String requestUserInfo(String accessToken);
    SocialType type();
}
