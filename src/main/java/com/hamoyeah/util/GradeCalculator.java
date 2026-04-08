package com.hamoyeah.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component @RequiredArgsConstructor
public class GradeCalculator {
    public Map<String, Object> calculatePlusScore(int cctvCount, int lampCount) {
        int totalPlus = cctvCount + lampCount;
        int cctvScore = 0;
        int streetLampScore = 0;

        if (totalPlus > 0) {
            // CCTV는 3점 가중치, 가로등은 1점 가중치로 계산하는 로직 유지
            cctvScore = cctvCount >= 40 ? 60 : cctvCount >= 35 ? 45 :
                    cctvCount >= 28 ? 30 : cctvCount >= 15 ? 10 : cctvCount >= 10 ? 5 :0;
            streetLampScore = lampCount >= 130 ? 15 : lampCount >= 100 ? 10 :
                    lampCount >= 60 ? 5 : lampCount >= 30 ? 2 : 0;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cctvScore", cctvScore);
        result.put("streetLampScore", streetLampScore);
        System.out.println("cctvCount = " + cctvCount);
        System.out.println("lampCount = " + lampCount);
        System.out.println("PlusResult = " + result);
        return result;
    }
    public Map<String,Object> minusScore(double noiseAvg,int pm10, int pm25){   // 반경 500미터 안에 들때 실행하는 코드
         Map<String,Object> minusResult=new HashMap<>();
         int noiseScore=noiseAvg >= 80 ? 20 : noiseAvg >= 60 ? 15 :
                 noiseAvg >= 55 ? 10 : noiseAvg >= 50 ? 5 : 0 ;  // 소음점수
         int pm10Score = pm10 <= 30 ? 0 : pm10 <= 80 ? 5 : pm10 <= 150 ? 8 : 10;  // 환경부 공식 기준(미세먼지)
        int pm25Score = pm25 <= 15 ? 0 : pm25 <= 35 ? 5 : pm25 <= 75 ? 8 : 10;  // 초미세먼지
         int minusAirScore=pm10Score+pm25Score;
        minusResult.put("noiseScore",noiseScore); minusResult.put("airScore",minusAirScore);
        System.out.println("noiseAvg = " + noiseAvg);
        System.out.println("pm10 = " + pm10);
        System.out.println("pm25 = " + pm25);
        System.out.println("MinusResult = " + minusResult);
        return minusResult;
    }
    public Map<String,Object> noNoiseScore(int pm10,int pm25){  // 반경 500미터 안에 소음 측정한 데이터가 없을시
        Map<String,Object> minusResult=new HashMap<>();
        System.out.println("pm10 = " + pm10);
        System.out.println("pm25 = " + pm25);
        int pm10Score = pm10 <= 30 ? 0 : pm10 <= 80 ? 10 : pm10 <= 150 ? 15 : 20;  // 환경부 공식 기준(미세먼지)
        int pm25Score = pm25 <= 15 ? 0 : pm25 <= 35 ? 10 : pm25 <= 75 ? 15 : 20;  // 초미세먼지
        minusResult.put("pm10Score",pm10Score); minusResult.put("pm25Score",pm25Score);
        return minusResult;
    }

    public Map<String,Object> totalGrade(int cctvScore, int streetLampScore, int noiseScore, int airScore){ // 반경 500미터 안에 소음 측정한 데이터가 있을시 최종 등급 계산
         Map<String,Object> result=new HashMap<>();
        int totalScore=( cctvScore + streetLampScore ) - ( noiseScore + airScore ); // + 점수 최대 : 60, - 점수 최대 : 40
        String grade = totalScore >= 45 ? "A" : totalScore >= 30 ? "B" : totalScore >= 15 ? "C" : "D";
        result.put("totalScore",totalScore); result.put("grade",grade);
        System.out.println("grade = " + grade);
        return result;
    }

    public Map<String,Object> noNoiseGrade(int cctvScore, int streetLampScore, int pm10Score, int pm25Score){
        Map<String,Object> result=new HashMap<>();
        System.out.println("======================");
        System.out.println("   소음 데이터 없을 시   ");
        System.out.println("======================");
        int totalScore=( cctvScore + streetLampScore ) - ( pm10Score + pm25Score );
        result.put("totalScore",totalScore);
        String grade = totalScore >= 45 ? "A" : totalScore >= 30 ? "B" : totalScore >= 15 ? "C" : "D";
        result.put("grade",grade);
        System.out.println("grade = " + grade);
        return result;
    }
}
