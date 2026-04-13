package com.hamoyeah.user.controller;

import com.hamoyeah.util.scheduler.DataUpdateScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final DataUpdateScheduler dataUpdateScheduler;

    @PostMapping("/content-update")
    public ResponseEntity<?> contentUpdate() {
        dataUpdateScheduler.contentUpdate();
        return ResponseEntity.ok("업데이트 완료");
    }
}