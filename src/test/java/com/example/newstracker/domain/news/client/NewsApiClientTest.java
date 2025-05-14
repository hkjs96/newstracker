package com.example.newstracker.domain.news.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class NewsApiClientTest {

    @Autowired
    private NewsApiClient newsApiClient;

    @Test
    void 키워드에_대한_뉴스_정상_호출() {
        List<NewsApiClient.NewsItem> result = newsApiClient.fetchNewsForKeyword("AI");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        result.forEach(item -> System.out.println(item.title()));
    }

    @Test
    void 빈_키워드_또는_예외시_빈리스트_반환() {
        List<NewsApiClient.NewsItem> results = newsApiClient.fetchNewsForKeyword("");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void 존재하지_않는_API_KEY_사용시_빈리스트_반환() {
        // given: 잘못된 API Key 직접 대입 (테스트용 NewsApiClient 대체)
        NewsApiClient brokenApiClient = new NewsApiClient("INVALID-API-KEY", "INVALID-API-KEY");

        // when
        // 2025-05-14T16:56:12.665+09:00  WARN 9860 --- [newstracker] [    Test worker] c.e.n.domain.news.client.NewsApiClient   : 네이버 뉴스 API 호출 실패: keyword=인공지능, error=401 Unauthorized: "{"errorMessage":"NID AUTH Result Invalid (1000) : Authentication failed. (인증에 실패했습니다.)","errorCode":"024"}"
        List<NewsApiClient.NewsItem> result = brokenApiClient.fetchNewsForKeyword("인공지능");

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty()); // 🔥 핵심 검증 포인트
    }
}
