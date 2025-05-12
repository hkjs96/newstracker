package com.example.newstracker.controller;

import com.example.newstracker.common.jwt.UserDetailsImpl;
import com.example.newstracker.common.response.ApiResponse;
import com.example.newstracker.domain.keyword.Keyword;
import com.example.newstracker.domain.keyword.service.KeywordService;
import com.example.newstracker.dto.request.KeywordRequest;
import com.example.newstracker.dto.resposne.KeywordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Keyword", description = "키워드 등록/조회/삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<KeywordResponse>> addKeyword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody KeywordRequest request
    ) {
        Keyword keyword = keywordService.addKeyword(userDetails.user(), request.keyword());
        return ResponseEntity.ok(ApiResponse.ok(new KeywordResponse(keyword.getId(), keyword.getKeyword())));
    }

    @Operation(summary = "내 키워드 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<KeywordResponse>>> getMyKeywords(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<KeywordResponse> keywords = keywordService.findKeywordsByUser(userDetails.user()).stream()
                .map(k -> new KeywordResponse(k.getId(), k.getKeyword()))
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(keywords));
    }

    @Operation(summary = "키워드 삭제")
    @DeleteMapping("/{keywordId}")
    public ResponseEntity<ApiResponse<Void>> deleteKeyword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long keywordId
    ) {
        keywordService.deleteKeyword(keywordId, userDetails.user());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}