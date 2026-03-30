package com.hamoyeah.safety.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class SafetyResponseDto {
    private String grade;           // 등급 A/B/C/D
    private int totalScore;         // 최종 점수
    private int streetLampScore;    // 가로등 점수
    private int streetLampCount;    // 반경 내 가로등 수
    private int cctvScore;          // CCTV 점수
    private int cctvCount;          // 반경 내 CCTV 수
    private int noiseScore;         // 소음 점수
    private double noiseAvg;        // 평균 소음
    private int airScore;           // 대기질 점수
    private int pm10;            // 평균 미세먼지
    private int pm25;            // 평균 초미세먼지
    private List<Map<String,Object>> contents;  // 콘텐츠를 담은 곳
}