package com.hamoyeah.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component @RequiredArgsConstructor
public class GradeCalculator {
     private final DistanceCalculator distanceCalculator;
     public Map<String,Object> PlusPoint(double userLat,double userLng){
        Map<String,Integer> plusResult=distanceCalculator.countInRange(userLat,userLng);
        int cctvCount=plusResult.get("cctv"); int lightCount=plusResult.get("streetLight");
        int plusCount=cctvCount+lightCount;
        int cctvScore=0; int streetLampScore=0;
        if(plusCount>0){
            int convertedTotal=cctvCount+lightCount/3;  // cctv:3 lamp:1 비율
            cctvScore=(int) ( ( cctvCount/(double)convertedTotal ) * 60 );
            streetLampScore=(int) ( ( ( lightCount/3.0 )/convertedTotal ) * 60);
        }
        Map<String,Object> result=new HashMap<>();
        result.put("cctvScore",cctvScore); result.put("streetLampScore",streetLampScore);
         System.out.println("result = " + result);
        return result;
     }
    public Map<String,Object> minusScore(double noiseAvg,int pm10, int pm25){
         Map<String,Object> minusResult=new HashMap<>();
         int noiseScore=noiseAvg >= 100 ? 20 : noiseAvg >= 90 ? 18 : noiseAvg >= 80 ?   // 소음 점수
                 16 : noiseAvg >= 70 ? 13 : noiseAvg >= 60 ? 10 : noiseAvg >= 40 ? 5 : 0;
         int pm10Score = pm10 <= 30 ? 0 : pm10 <= 80 ? 5 : pm10 <= 150 ? 8 : 10;  // 환경부 공식 기준(미세먼지)
        int pm25Score = pm25 <= 15 ? 0 : pm25 <= 35 ? 5 : pm25 <= 75 ? 8 : 10;  // 초미세먼지
         int minusAirScore=pm10Score+pm25Score;
        minusResult.put("noiseScore",noiseScore); minusResult.put("airScore",minusAirScore);
        System.out.println("MinusResult = " + minusResult);
        return minusResult;
    }

    public Map<String,Object> totalGrade(int cctvScore, int streetLampScore, int noiseScore, int airScore){
         Map<String,Object> result=new HashMap<>();
        int totalScore=( cctvScore + streetLampScore ) - ( noiseScore + airScore );
        String grade=totalScore < 40 ? "D" : totalScore < 60 ? "C" : totalScore < 80 ? "B" : "A" ;
        result.put("totalScore",totalScore); result.put("grade",grade);
        return result;
    }
}
