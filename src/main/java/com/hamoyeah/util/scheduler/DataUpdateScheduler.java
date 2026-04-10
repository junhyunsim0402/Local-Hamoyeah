package com.hamoyeah.util.scheduler;

import com.hamoyeah.contents.service.ContentsService;
import com.hamoyeah.news.service.NewsService;
import com.hamoyeah.safety.service.SafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataUpdateScheduler {
    private final ContentsService contentsService;
    private final SafetyService safetyService;
    private final NewsService newsService;

    @Scheduled(cron = "0 0 3 * * MON")
    public void contentUpdate(){
        System.out.println("컨텐츠 데이터 업데이트 시작");
        // 1. 관광 카테고리 ID: 1
        contentsService.contentApi("https://www.jinju.go.kr/openapi/tour/tourinfo.do", 1);

        // 2. 축제 카테고리 ID: 2
        contentsService.contentApi("https://www.jinju.go.kr/openapi/tour/festival.do", 2);

        // 3. 문화재 카테고리 id 3
        contentsService.contentApi("https://www.jinju.go.kr/openapi/tour/asset.do", 3);

        // 4. 공공 체육시설 카테고리 id 4
        contentsService.dataApi("https://api.odcloud.kr/api/15128089/v1/uddi:cdad3c8e-652f-4559-a635-868c5e109cc3", 4);

        // 5. 건축 미술 카테고리 id 5
        contentsService.dataApi("https://api.odcloud.kr/api/15116581/v1/uddi:1dc95d0f-13c4-4094-a2c4-8cf29e76a527", 5);

        // 6. 공공 미술 카테고리 id 6
        contentsService.dataApi("https://api.odcloud.kr/api/15099262/v1/uddi:4adcb840-f974-4de6-b48f-d1be8a047eaa", 6);

        System.out.println("컨텐츠 데이터 업데이트 완료");
    }

    @Scheduled(cron = "0 0 5 1 * *")
    public void safetyUpdate(){
        System.out.println("cctv,가로등 데이터 업데이트 시작");
        safetyService.syncStreetLamp("https://api.odcloud.kr/api/15113209/v1/uddi:26bff442-7a44-4c35-a657-f863ceab48db");
        safetyService.syncCctv("https://api.odcloud.kr/api/15143299/v1/uddi:47bc7b31-8e0f-42d0-9137-f5d85640fbaa");
        System.out.println("cctv,가로등  데이터 업데이트 완료");

    }

    @Scheduled(cron = "0 0 9 * * *")  // 매일 오전 9시
    public void newsUpdate(){
        System.out.println("뉴스 캐시 갱신 시작");
        newsService.refreshAllNewsCache();
        System.out.println("뉴스 캐시 갱신 완료");
    }

}
