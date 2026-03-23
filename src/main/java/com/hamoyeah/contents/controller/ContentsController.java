package com.hamoyeah.contents.controller;

import com.hamoyeah.contents.service.ContentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ContentsController {
    private final ContentsService contentsService;
    @GetMapping("/api/sync")
    public String syncData() {
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


        return "모든 데이터 동기화 완료!";
    }
}
