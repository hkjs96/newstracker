package com.example.newstracker.domain.news.service;

import com.example.newstracker.domain.keyword.Keyword;
import com.example.newstracker.domain.news.News;
import com.example.newstracker.domain.news.repository.NewsRepository;
import com.example.newstracker.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;

    public List<News> findNewsByUser(User user) {
        return newsRepository.findAllByUserOrderByPublishedAtDesc(user);
    }

    @Transactional
    public void saveNewsIfNotExists(User user, Keyword keyword, String title, String summary, String link, LocalDateTime publishedAt) {
        if (!newsRepository.existsByUserAndLink(user, link)) {
            News news = News.builder()
                    .user(user)
                    .keyword(keyword)
                    .title(title)
                    .summary(summary)
                    .link(link)
                    .publishedAt(publishedAt)
                    .build();
            newsRepository.save(news);
        }
    }
}