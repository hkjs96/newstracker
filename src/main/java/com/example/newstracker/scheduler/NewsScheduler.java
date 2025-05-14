package com.example.newstracker.scheduler;

import com.example.newstracker.domain.keyword.Keyword;
import com.example.newstracker.domain.keyword.repository.KeywordRepository;
import com.example.newstracker.domain.news.client.NewsApiClient;
import com.example.newstracker.domain.news.service.NewsService;
import com.example.newstracker.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final KeywordRepository keywordRepository;
    private final NewsApiClient newsApiClient;
    private final NewsService newsService;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("Asia/Seoul"));

    @Scheduled(fixedDelay = 600_000) // 10분마다 실행
    public void collectNews() {
        log.info("🔄 뉴스 수집 시작");

        var keywords = keywordRepository.findAll();

        for (Keyword keyword : keywords) {
            User user = keyword.getUser();
            String value = keyword.getKeyword();

            try {
                var items = newsApiClient.fetchNewsForKeyword(value);

                items.forEach(item -> {
                    try {
                        LocalDateTime publishedAt = ZonedDateTime
                                .parse(item.pubDate(), formatter)
                                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                                .toLocalDateTime();

                        newsService.saveNewsIfNotExists(
                                user,
                                keyword,
                                item.title(),
                                item.description(),
                                item.link(),
                                publishedAt
                        );
                    } catch (Exception ex) {
                        log.warn("❌ 뉴스 저장 실패: {}", ex.getMessage());
                    }
                });

                log.info("✅ {} 키워드 뉴스 수집 완료 ({}건)", value, items.size());

            } catch (Exception e) {
                log.warn("⚠️ {} 키워드 수집 실패: {}", value, e.getMessage());
            }
        }

        log.info("✅ 전체 뉴스 수집 완료");
    }
}
