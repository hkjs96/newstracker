package com.example.newstracker.domain.news.client;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Service
public class NewsApiClient {

    private final RestClient restClient;

    public NewsApiClient(
            @Value("${naver.client-id}") String clientId,
            @Value("${naver.client-secret}") String clientSecret
    ) {
        this.restClient = RestClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/news.json")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    public List<NewsItem> fetchNewsForKeyword(String keyword) {
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("query", keyword)
                            .queryParam("display", 10)
                            .queryParam("sort", "date")
                            .build())
                    .retrieve()
                    .body(NaverNewsResponse.class);

            if (response == null || response.items() == null) {
                log.warn("네이버 뉴스 응답이 null입니다. keyword={}", keyword);
                return List.of();
            }

            return response.items();

        } catch (Exception e) {
            log.warn("네이버 뉴스 API 호출 실패: keyword={}, error={}", keyword, e.getMessage());
            return List.of();
        }
    }

    public record NaverNewsResponse(List<NewsItem> items) {}

    public record NewsItem(String title, String description, String link, String pubDate) {}
}
