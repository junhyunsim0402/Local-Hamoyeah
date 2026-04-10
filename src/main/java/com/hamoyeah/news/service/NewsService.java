package com.hamoyeah.news.service;

import com.hamoyeah.news.dto.NewsDto;
import com.hamoyeah.news.entity.NewsCategory;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j  // 에러가 났을 때 알려주는 어노테이션
public class NewsService {

    private static final int MAX_NEWS = 10; // 카테고리당 최대 뉴스 수
    private static final String BASE_URL = "https://www.jinjutv.com/news/articleList.html"; // 셀레니움 URL

    private WebDriver createDriver(){         // 크롬 브라우저(뉴스 창) 설치/설정
        WebDriverManager.chromedriver().setup();    // 크롬 드라이버 설치
        ChromeOptions options=new ChromeOptions();
        options.addArguments("--headless");     // 크롬 브라우저 창 숨기기
        options.addArguments("--disable-gpu"); // GPU관련 오류 방지
        options.addArguments("--disable-dev-shm"); // 메모리 공유
        options.addArguments("--no-sandbox"); // 권한 부여
        return new ChromeDriver(options);
    }   // 크롬 브라우저(뉴스 창) 설치/설정 끝

    public List<NewsDto> getNewsByCategory(int contentType, int contentCategory) {  // 카테고리로 찾은 뉴스 함수
        NewsCategory newsCategory = NewsCategory.from(contentType, contentCategory);
        if (newsCategory == null) { return List.of(); }

        List<NewsDto> result = new ArrayList<>();   // 결과를 담을 리스트
        WebDriver driver=createDriver();    // 크롬 브라우저 객체

        try{
            for(String keyword : newsCategory.getKeywords()){      // 데이터 추출
                if(result.size()>=MAX_NEWS) break;  // 10개 이하로 가져옴

                String url=BASE_URL + "?sc_word="
                        + java.net.URLEncoder.encode(keyword, "UTF-8")
                        + "&view_type=sm";
                driver.get(url);    // url 전달

                try {
                    new WebDriverWait(driver, Duration.ofSeconds(10))    // 렌더링 5초 기다림
                            .until(ExpectedConditions.presenceOfElementLocated( // 리스트 페이지 나올때까지 대기
                                    By.cssSelector("ul.type2 > li")));  // 뉴스 리스트
                }catch (Exception e){
                    log.warn("렌더링 대기 실패 - keyword: {}", keyword);
                    continue;
                }
                Document doc = Jsoup.parse(driver.getPageSource()); // 셀레니움이 페이지 열고 렌더링 완료되면 html -> jsoup 파싱
                Elements articles = doc.select("ul.type2 > li");    // 랜더링된 뉴스 전체 가져오기

                for (Element article : articles) {
                    if (result.size() >= MAX_NEWS) break;

                    Element titleEl = article.selectFirst("h4.titles a");
                    Element dateEl = article.selectFirst("span.byline em:last-child");

                    if (titleEl == null) continue;

                    // 중복 제거
                    boolean isDuplicate = result.stream()
                            .anyMatch(r -> r.getTitle().equals(titleEl.text()));
                    if (isDuplicate) continue;

                    result.add(NewsDto.builder()
                            .title(titleEl.text())
                            .url("https://www.jinjutv.com" + titleEl.attr("href"))
                            .date(dateEl != null ? dateEl.text() : "")
                            .build());

                    System.out.println("result = " + result);
                }
            }
        }catch (Exception e) { log.error("RSS 크롤링 실패: {}", e.getMessage()); }
        finally { driver.quit(); }
        return result;
    }   // 카테고리로 찾은 뉴스 함수 끝
}