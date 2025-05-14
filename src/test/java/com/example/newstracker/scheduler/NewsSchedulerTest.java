package com.example.newstracker.scheduler;

import com.example.newstracker.domain.keyword.Keyword;
import com.example.newstracker.domain.keyword.repository.KeywordRepository;
import com.example.newstracker.domain.news.client.NewsApiClient;
import com.example.newstracker.domain.news.repository.NewsRepository;
import com.example.newstracker.domain.news.service.NewsService;
import com.example.newstracker.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = NewsScheduler.class)
@Import({
        NewsScheduler.class
})
class NewsSchedulerTest {

    @MockitoBean
    private KeywordRepository keywordRepository;

    @MockitoBean
    private NewsRepository newsRepository;

    @MockitoBean
    private NewsApiClient newsApiClient;

    @MockitoBean
    private NewsService newsService;

    @Autowired
    private NewsScheduler newsScheduler;

    @Test
    void 키워드별_뉴스_수집_중_일부_실패해도_전체_흐름은_유지된다() {
        // given
        User user = User.builder().id(1L).email("test@test.com").googleId("123").nickname("tester").build();
        Keyword successKeyword = Keyword.builder().id(1L).user(user).keyword("AI").build();
        Keyword failKeyword = Keyword.builder().id(2L).user(user).keyword("Broken").build();

        given(keywordRepository.findAll()).willReturn(List.of(successKeyword, failKeyword));

        String validDate = "Wed, 14 May 2025 08:23:00 GMT";

        given(newsApiClient.fetchNewsForKeyword("AI")).willReturn(List.of(
                new NewsApiClient.NewsItem("제목1", "요약1", "https://news/1", validDate)
        ));

        given(newsApiClient.fetchNewsForKeyword("Broken"))
                .willThrow(new RuntimeException("API 실패 시뮬레이션"));

        // when
        newsScheduler.collectNews();

        // then
        verify(newsService, atLeastOnce()).saveNewsIfNotExists(
                eq(user), eq(successKeyword), any(), any(), any(), any()
        );

        verify(newsService, never()).saveNewsIfNotExists(eq(user), eq(failKeyword), any(), any(), any(), any());
    }

    @Test
    void 뉴스_저장_중_예외가_발생해도_다음_뉴스로_계속_진행된다() {
        // given
        User user = User.builder().id(1L).email("user@test.com").googleId("g123").nickname("user").build();
        Keyword keyword = Keyword.builder().id(1L).user(user).keyword("AI").build();

        given(keywordRepository.findAll()).willReturn(List.of(keyword));

        List<NewsApiClient.NewsItem> items = List.of(
                new NewsApiClient.NewsItem("뉴스1", "설명1", "https://news/1", "Wed, 14 May 2025 08:23:00 GMT"),
                new NewsApiClient.NewsItem("뉴스2", "설명2", "https://news/2", "Wed, 14 May 2025 08:25:00 GMT")
        );

        given(newsApiClient.fetchNewsForKeyword("AI")).willReturn(items);

        // 첫 번째 뉴스 저장 시도 시 예외 발생
        doThrow(new RuntimeException("DB 저장 실패"))
                .doNothing() // 두 번째는 정상 저장
                .when(newsService).saveNewsIfNotExists(
                        eq(user), eq(keyword), any(), any(), any(), any()
                );

        // when
        newsScheduler.collectNews();

        // then
        verify(newsService, times(2)).saveNewsIfNotExists(
                eq(user), eq(keyword), any(), any(), any(), any()
        );
    }

    @Test
    void 동일한_뉴스_링크는_중복_저장되지_않는다() {
        // given
        User user = User.builder().id(1L).email("test@test.com").googleId("g123").nickname("tester").build();
        Keyword keyword = Keyword.builder().id(1L).user(user).keyword("AI").build();
        String link = "https://news/1";

        given(newsRepository.existsByUserAndLink(user, link)).willReturn(true); // 이미 존재함

        // when
        newsService.saveNewsIfNotExists(
                user, keyword,
                "제목", "설명",
                link,
                LocalDateTime.now()
        );

        // then
        verify(newsRepository, never()).save(any());
    }

}