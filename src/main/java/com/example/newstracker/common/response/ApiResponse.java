package com.example.newstracker.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 응답 구조")
public record ApiResponse<T>(
        @Schema(description = "성공 여부") boolean success,
        @Schema(description = "응답 데이터") T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data);
    }
}
