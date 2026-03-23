package com.hamoyeah.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component @RequiredArgsConstructor
public class GradeCalculator {
     private final DistanceCalculator distanceCalculator;
     public Map<String,Object> PlusPoint(double userLat,double userLng){
        Map<String,Integer> plusResult=distanceCalculator.countInRange(userLat,userLng);
        int cctvCount=plusResult.get("cctv"); int lightCount=plusResult.get("streetLight");
        int plusCount=cctvCount+lightCount;
        int cctvScore=0; int lampScore=0;
        if(plusCount>0){
            int convertedTotal=cctvCount+lightCount/3;  // cctv:3 lamp:1 비율
            cctvScore=(int)((cctvCount/(double)convertedTotal)*60);
            lampScore=(int)(((lightCount/3.0)/convertedTotal)*60);
        }
        Map<String,Object> result=new HashMap<>();
        result.put("cctvPoint",cctvScore); result.put("lampPoint",lampScore);
         System.out.println("result = " + result);
        return result;
     }
    public int MinusScore(double noiseAvg){
         int noiseScore=noiseAvg >= 100 ? 20 : noiseAvg >= 90 ? 18 : noiseAvg >= 80 ?
                 16 : noiseAvg >= 70 ? 13 : noiseAvg >= 60 ? 10 : noiseAvg >= 40 ? 5 : 0;
         int minusAirScore=0; // TODO : 미세먼지 해야함
        return noiseScore+minusAirScore;    // TODO : 미구현
    }
}
