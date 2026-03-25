package com.hamoyeah.safety.service;

import com.hamoyeah.safety.dto.SafetyRequestDto;
import com.hamoyeah.safety.dto.SafetyResponseDto;
import com.hamoyeah.util.DistanceCalculator;
import com.hamoyeah.util.GradeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class SafetyService {
    private final DistanceCalculator distanceCalculator;
    private final GradeCalculator gradeCalculator;

    public SafetyResponseDto getSafety(SafetyRequestDto requestDto) {
        // 1. API 데이터 수집 (현재는 빈 리스트)
        List<Map<String, Object>> cctvRawData = fetchCctvFromApi();
        List<Map<String, Object>> lampRawData = fetchLampFromApi();

        // 2. 반경 내 개수 계산 (DistanceCalculator 활용)
        int cctvCount = distanceCalculator.calculateCount(
                requestDto.getLat(), requestDto.getLng(), requestDto.getRadius(), cctvRawData);
        int lampCount = distanceCalculator.calculateCount(
                requestDto.getLat(), requestDto.getLng(), requestDto.getRadius(), lampRawData);

        // 3. 점수 환산 (GradeCalculator 활용)
        Map<String, Object> plusResult = gradeCalculator.calculatePlusScore(cctvCount, lampCount);
        int cctvScore = (int) plusResult.get("cctvScore");
        int streetLampScore = (int) plusResult.get("streetLampScore");

        // 4. 위험 점수 및 최종 등급 계산
        Map<String, Object> minusResult = gradeCalculator.minusScore(55.0, 45, 20);
        int noiseScore = (int) minusResult.get("noiseScore");
        int airScore = (int) minusResult.get("airScore");

        Map<String, Object> totalResult = gradeCalculator.totalGrade(cctvScore, streetLampScore, noiseScore, airScore);

        return SafetyResponseDto.builder()
                .grade(totalResult.get("grade").toString())
                .totalScore((int) totalResult.get("totalScore"))
                .cctvScore(cctvScore)
                .streetLampScore(streetLampScore)
                .noiseScore(noiseScore)
                .airScore(airScore)
                .build();
    }
    private List<Map<String, Object>> fetchCctvFromApi() {
        return null;
    }

    private List<Map<String, Object>> fetchLampFromApi() {
        return null;
    }
}