package com.example.newstracker.dto.resposne;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "키워드 응답")
public record KeywordResponse(
        @Schema(description = "키워드 ID") Long id,
        @Schema(description = "키워드 내용") String keyword
) {}