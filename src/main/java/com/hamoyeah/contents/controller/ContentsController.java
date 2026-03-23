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
        return "모든 데이터 동기화 완료!";
    }
}
