package com.example.newstracker.config;

import com.example.newstracker.common.jwt.JwtProvider;
import com.example.newstracker.common.jwt.UserDetailsImpl;
import com.example.newstracker.domain.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        // OAuth 로그인 완료된 사용자 정보
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//        User user = userDetails.user();

        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(userDetails);

        // 🎯 JWT를 프론트로 전달하는 방식 선택
        // 예시 1: 쿼리 파라미터로
        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:3000/oauth-success")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
