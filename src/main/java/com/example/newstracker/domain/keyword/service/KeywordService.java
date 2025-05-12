package com.example.newstracker.domain.keyword.service;

import com.example.newstracker.domain.keyword.Keyword;
import com.example.newstracker.domain.keyword.repository.KeywordRepository;
import com.example.newstracker.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public List<Keyword> findKeywordsByUser(User user) {
        return keywordRepository.findAllByUser(user);
    }

    @Transactional
    public Keyword addKeyword(User user, String keyword) {
        return keywordRepository.save(Keyword.builder()
                .user(user)
                .keyword(keyword)
                .build());
    }

    @Transactional
    public void deleteKeyword(Long keywordId, User user) {
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new IllegalArgumentException("Keyword not found"));
        if (!keyword.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized access");
        }
        keywordRepository.delete(keyword);
    }
}
