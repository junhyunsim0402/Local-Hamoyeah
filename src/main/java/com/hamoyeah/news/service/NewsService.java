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
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j  // 에러가 났을 때 알려주는 어노테이션
public class NewsService {

    private static final int MAX_NEWS = 10; // 카테고리당 최대 뉴스 수

    public List<NewsDto> getNewsByCategory(int contentType, int contentCategory) {  // 카테고리로 찾은 뉴스 함수
        NewsCategory newsCategory = NewsCategory.from(contentType, contentCategory);
        if (newsCategory == null) {
            return List.of();
        }

        List<NewsDto> result = new ArrayList<>();   // 결과를 담을 리스트

        try{
            Document doc = Jsoup.connect("https://www.jinjutv.com/rss/allArticle.xml")  // 크롤링 주소 RSS
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .parser(org.jsoup.parser.Parser.xmlParser())    // RSS(XML) --> JSOUP으로 읽고 Document로 반환
                    .get();
            Elements items = doc.select("item");        // 뉴스 추출

            for(Element item : items){      // 데이터 추출
                if(result.size()>=MAX_NEWS) break;

                String title=item.selectFirst("title").text();

                boolean matched = Arrays.stream(newsCategory.getKeywords())
                        .anyMatch(title::contains); // 키워드 배열에 필터링되면 true

                if(!matched) continue;  // 필터링안되면 무시

                result.add(NewsDto.builder()
                        .title(title)
                        .url(item.selectFirst("link").text())
                        .date(item.selectFirst("pubDate")!=null
                                ? item.selectFirst("pubDate").text() : "" )
                        .build()
                );
            }
        }catch (IOException e) { log.error("RSS 크롤링 실패: {}", e.getMessage()); }

        return result;
    }   // 카테고리로 찾은 뉴스 함수 끝
}