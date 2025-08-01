package com.example.yozi.user.entity;

import com.example.yozi.user.entity.enums.SocialType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class SocialTypeConverter implements Converter<String, SocialType> {

    @Override
    public SocialType convert(String s) {
        return SocialType.valueOf(s.toUpperCase());
    }
}