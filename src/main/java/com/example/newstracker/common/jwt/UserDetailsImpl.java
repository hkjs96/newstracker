package com.example.newstracker.common.jwt;

import com.example.newstracker.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Map;

public record UserDetailsImpl(
        User user,
        Map<String, Object> attributes
) implements UserDetails, OAuth2User {

    public UserDetailsImpl(User user) {
        this(user, Collections.emptyMap()); // ✅ 오버로드된 생성자 역할
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getGoogleId(); // OAuth2User 요구: 고유 식별자
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return null;
    }
}
