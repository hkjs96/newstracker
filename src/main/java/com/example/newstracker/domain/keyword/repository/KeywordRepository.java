package com.example.newstracker.domain.keyword.repository;

import com.example.newstracker.domain.keyword.Keyword;
import com.example.newstracker.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findAllByUser(User user);
}
