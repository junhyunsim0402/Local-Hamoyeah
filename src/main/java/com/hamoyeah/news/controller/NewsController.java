package com.hamoyeah.news.controller;

import com.hamoyeah.news.dto.NewsDto;
import com.hamoyeah.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NewsController {

    private final NewsService newsService;

    // 카테고리별 뉴스 조회
    // GET /api/news?contentType=1&contentCategory=1
    @GetMapping
    public ResponseEntity<List<NewsDto>> getNews(
            @RequestParam int contentType,
            @RequestParam int contentCategory) {

        List<NewsDto> news = newsService.getNewsByCategory(contentType, contentCategory);
        return ResponseEntity.ok(news);
    }
}