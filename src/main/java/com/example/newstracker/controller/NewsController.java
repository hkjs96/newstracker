package com.example.newstracker.controller;

import com.example.newstracker.common.jwt.UserDetailsImpl;
import com.example.newstracker.common.response.ApiResponse;
import com.example.newstracker.domain.news.News;
import com.example.newstracker.domain.news.service.NewsService;
import com.example.newstracker.dto.resposne.NewsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "JWT")
@Tag(name = "News", description = "뉴스 수집/조회 API")
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "내 뉴스 목록 조회")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<NewsResponseDto>>> getMyNews(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<News> newsList = newsService.findNewsByUser(userDetails.user());

        List<NewsResponseDto> response = newsList.stream()
                .map(news -> new NewsResponseDto(
                        news.getTitle(),
                        news.getSummary(),
                        news.getLink(),
                        news.getPublishedAt()
                ))
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
