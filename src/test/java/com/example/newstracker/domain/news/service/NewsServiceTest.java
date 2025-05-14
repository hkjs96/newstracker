package com.example.newstracker.domain.news.service;

import com.example.newstracker.domain.keyword.Keyword;
import com.example.newstracker.domain.news.News;
import com.example.newstracker.domain.news.repository.NewsRepository;
import com.example.newstracker.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Import(NewsService.class)
class NewsServiceTest {

    @MockitoBean
    private NewsRepository newsRepository;

    @Autowired
    private NewsService newsService;

    @Test
    void 중복이_아닌_뉴스는_저장된다() {
        // given
        User user = User.builder().id(1L).email("test@test.com").googleId("g123").nickname("tester").build();
        Keyword keyword = Keyword.builder().id(1L).user(user).keyword("AI").build();
        String link = "https://news/unique";

        // 중복 아님
        given(newsRepository.existsByUserAndLink(user, link)).willReturn(false);

        // 저장 동작도 지정
        given(newsRepository.save(any())).willReturn(
                News.builder().id(1L).user(user).keyword(keyword).title("제목").link(link).build()
        );

        // when
        newsService.saveNewsIfNotExists(
                user, keyword,
                "제목", "설명",
                link,
                LocalDateTime.now()
        );

        // then
        verify(newsRepository, times(1)).save(any());
    }
}
