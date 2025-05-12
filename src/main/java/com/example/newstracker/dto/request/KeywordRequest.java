package com.example.newstracker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "키워드 등록 요청")
public record KeywordRequest(
        @Schema(description = "등록할 키워드", example = "인공지능")
        String keyword
) {}