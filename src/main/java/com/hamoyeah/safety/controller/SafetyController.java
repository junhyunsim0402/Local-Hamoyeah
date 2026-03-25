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

    @GetMapping("/sync")
    public String syncSafety(){
        safetyService.syncStreetLamp("https://api.odcloud.kr/api/15113209/v1/uddi:26bff442-7a44-4c35-a657-f863ceab48db");
        safetyService.syncCctv("https://api.odcloud.kr/api/15143299/v1/uddi:47bc7b31-8e0f-42d0-9137-f5d85640fbaa");
        return "가로등 및 cctv 수집 완료";
    }
}
