package com.hamoyeah.fav.controller;

import com.hamoyeah.fav.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/top2")
    public ResponseEntity<List<Map<String, Object>>> getTop2() {
        return ResponseEntity.ok(recommendService.getTop2());
    }
}