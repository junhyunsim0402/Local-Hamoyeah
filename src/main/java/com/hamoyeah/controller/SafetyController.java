package com.hamoyeah.controller;

import com.hamoyeah.dto.SafetyRequestDto;
import com.hamoyeah.dto.SafetyResponseDto;
import com.hamoyeah.service.SafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor @RequestMapping("/api/safety")
public class SafetyController {
    private final SafetyService safetyService;
    @GetMapping("/") public ResponseEntity<SafetyResponseDto> getSafety(@RequestBody SafetyRequestDto requestDto){   //
        return ResponseEntity.ok(safetyService.getSafety(requestDto));
    }
}
