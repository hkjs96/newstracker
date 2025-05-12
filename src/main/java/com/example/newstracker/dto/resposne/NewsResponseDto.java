package com.example.newstracker.dto.resposne;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "뉴스 응답 DTO")
public record NewsResponseDto(
        @Schema(description = "뉴스 제목") String title,
        @Schema(description = "요약") String summary,
        @Schema(description = "링크") String link,
        @Schema(description = "발행 시각") LocalDateTime publishedAt
) {}