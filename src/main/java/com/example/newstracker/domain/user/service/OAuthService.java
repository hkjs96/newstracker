package com.example.newstracker.domain.user.service;

import com.example.newstracker.common.jwt.UserDetailsImpl;
import com.example.newstracker.domain.user.User;
import com.example.newstracker.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String sub = oAuth2User.getAttribute("sub");
        String nickname = oAuth2User.getAttribute("name");

        // 기존 사용자 조회 또는 등록
        User user = userRepository.findByGoogleId(sub)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .googleId(sub)
                        .nickname(nickname)
                        .build()));

        // OAuth2User (Spring Security 내부 사용용) 생성
        return new UserDetailsImpl(user, oAuth2User.getAttributes());
    }
}
