package com.hamoyeah.news.service;

import com.hamoyeah.news.dto.NewsDto;
import com.hamoyeah.news.entity.NewsCategory;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j  // 에러가 났을 때 알려주는 어노테이션
public class NewsService {

    private static final String BASE_URL = "https://www.jinjutv.com/news/articleList.html"; // 크롤링 주소
    private static final int MAX_NEWS = 10; // 카테고리당 최대 뉴스 수

    public List<NewsDto> getNewsByCategory(int contentType, int contentCategory) {  // 카테고리로 찾은 뉴스 함수
        NewsCategory newsCategory = NewsCategory.from(contentType, contentCategory);
        if (newsCategory == null) {
            return List.of();
        }

        List<NewsDto> result = new ArrayList<>();

        // 키워드별로 크롤링 후 합산 (중복 제거)
        for (String keyword : newsCategory.getKeywords()) {
            if (result.size() >= MAX_NEWS) break;
            try {
                List<NewsDto> items = crawl(keyword, MAX_NEWS - result.size());
                for (NewsDto item : items) {
                    // 제목 중복 제거
                    boolean isDuplicate = result.stream()
                            .anyMatch(r -> r.getTitle().equals(item.getTitle()));
                    if (!isDuplicate) {
                        result.add(item);
                    }
                }
            } catch (IOException e) {
                log.error("크롤링 실패 - keyword: {}, error: {}", keyword, e.getMessage());
            }
        }

        return result;
    }   // 카테고리로 찾은 뉴스 함수 끝

    private List<NewsDto> crawl(String keyword, int limit) throws IOException {
        String url = BASE_URL + "?sc_word=" + java.net.URLEncoder.encode(keyword, "UTF-8") + "&view_type=sm";

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(5000)
                .get();

        Elements articles = doc.select("ul.type2 > li");

        List<NewsDto> items = new ArrayList<>();
        for (Element article : articles) {
            if (items.size() >= limit) break;

            Element titleEl = article.selectFirst("h4.titles a");
            Element dateEl  = article.selectFirst("em.info span");
            Element cateEl  = article.selectFirst("em.info a");
            Element imgEl   = article.selectFirst("a.thumb img");

            if (titleEl == null) continue;

            items.add(NewsDto.builder()
                    .title(titleEl.text())
                    .url("https://www.jinjutv.com" + titleEl.attr("href"))
                    .date(dateEl != null ? dateEl.text() : "")
                    .category(cateEl != null ? cateEl.text() : "")
                    .build());
        }

        return items;
    }
}