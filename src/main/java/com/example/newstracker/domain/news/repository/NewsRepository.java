package com.example.newstracker.domain.news.repository;

import com.example.newstracker.domain.news.News;
import com.example.newstracker.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByUserOrderByPublishedAtDesc(User user);
    boolean existsByUserAndLink(User user, String link); // 중복 방지용
}