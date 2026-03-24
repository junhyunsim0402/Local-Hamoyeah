package com.hamoyeah.safety.service;

import com.hamoyeah.safety.dto.SafetyRequestDto;
import com.hamoyeah.safety.dto.SafetyResponseDto;
import com.hamoyeah.util.DistanceCalculator;
import com.hamoyeah.util.GradeCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service @RequiredArgsConstructor @Transactional
public class SafetyService {
    private final DistanceCalculator distanceCalculator;
    private final GradeCalculator gradeCalculator;
    public SafetyResponseDto getSafety(SafetyRequestDto requestDto){
        double userLat=requestDto.getLat(); // 유저의 위도
        double userLng=requestDto.getLng(); // 유저의 경도

        // 안전 점수 계산
        Map<String,Object> plusResult=gradeCalculator.PlusPoint(userLat,userLng);   // 사용자의 위도/경도 주입
        int cctvScore=(int) plusResult.get("cctvScore");
        int streetLampScore=(int) plusResult.get("streetLampScore");

        // TODO : api로 가져올 소음/미세먼지/초미세먼지 데이터 현재는 샘플데이터
        double noiseAvg=55.0;   // 5점
        int pm10=45;            // 5점
        int pm25=20;            // 5점

        // 위험 점수 계산
        Map<String,Object> minusResult=gradeCalculator.minusScore(noiseAvg,pm10,pm25);
        int noiseScore=(int) minusResult.get("noiseScore");
        int airScore=(int) minusResult.get("airScore");

        // 등급 및 점수
        Map<String,Object> result=gradeCalculator.totalGrade(cctvScore,streetLampScore,noiseScore,airScore);
        System.out.println("result = " + result);
        return SafetyResponseDto.builder()
                .grade(result.get("grade").toString())
                .totalScore((int) result.get("totalScore"))
                .cctvScore(cctvScore)
                .streetLampScore(streetLampScore)
                .noiseScore(noiseScore)
                .airScore(airScore)
                .pm10(pm10)
                .pm25(pm25)
                .build();
    }
}
