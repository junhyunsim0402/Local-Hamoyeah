package com.hamoyeah.safety.controller;

import com.hamoyeah.safety.dto.SafetyRequestDto;
import com.hamoyeah.safety.dto.SafetyResponseDto;
import com.hamoyeah.safety.service.SafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor @RequestMapping("/api/safety")
public class SafetyController {
    private final SafetyService safetyService;
    @PostMapping("/") public ResponseEntity<SafetyResponseDto> getSafety(@RequestBody SafetyRequestDto requestDto){
        return ResponseEntity.ok(safetyService.getSafety(requestDto));
    }

    @GetMapping("/sync/safety")
    public String syncSafety(){
        safetyService.syncStreetLamp("https://api.odcloud.kr/api/15113209/v1/uddi:26bff442-7a44-4c35-a657-f863ceab48db");
        safetyService.syncCctv("https://api.odcloud.kr/api/15143299/v1/uddi:47bc7b31-8e0f-42d0-9137-f5d85640fbaa");
        return "가로등 및 cctv 수집 완료";
    }

    @GetMapping("/contents") public ResponseEntity<?> getContents(@ModelAttribute SafetyRequestDto requestDto){
        return ResponseEntity.ok(safetyService.getContents(requestDto));
    }   // 모든 컨텐츠 조회

    @GetMapping("/contents/category") public ResponseEntity<?> getContentsByCategory(
            @ModelAttribute SafetyRequestDto requestDto,
            @RequestParam Integer categoryId){
        return ResponseEntity.ok(safetyService.getContentsByCategory(requestDto,categoryId));
    }   // contents 카테고리별로 분류

    @GetMapping("/shop/category") public ResponseEntity<?> getShopByCategory(
            @ModelAttribute SafetyRequestDto requestDto,
            @RequestParam String shopCategory){
        return ResponseEntity.ok(safetyService.getShopByCategory(requestDto,shopCategory));
    }   // shop 카테고리별로 분류

    // 50m 반경 contents,shop 목록 가져오는 코드 (인증용)
    @GetMapping("/auth-contents")
    public ResponseEntity<?> getAuthContents(@ModelAttribute SafetyRequestDto requestDto) {
        return ResponseEntity.ok(safetyService.getAuthContents(requestDto));
    }

    @GetMapping("/sync/air")
    public boolean syncAir(){
        boolean result = safetyService.syncAir();
        return result;
    }

}
