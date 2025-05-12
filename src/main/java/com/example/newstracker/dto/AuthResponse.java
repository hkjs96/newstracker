package com.example.newstracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "OAuth 로그인 응답")
public record AuthResponse(
        @Schema(description = "사용자 ID") Long userId,
        @Schema(description = "닉네임") String nickname,
        @Schema(description = "Access Token") String accessToken,
        @Schema(description = "Refresh Token") String refreshToken
) {}
