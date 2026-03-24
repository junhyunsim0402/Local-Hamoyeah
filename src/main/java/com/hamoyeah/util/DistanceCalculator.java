package com.hamoyeah.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DistanceCalculator {
    public double distance(double lat1,double lat2,double lng1,double lng2){
        double bottom=Math.abs(lat1-lat2);  // 절댓값으로 음수 방지
        double height=Math.abs(lng1-lng2);

        double hypotenuse=Math.sqrt(bottom*bottom+height*height);   // 빗변의 길이 Math.sqrt는 제곱근 함수

        return hypotenuse;
    }   // 두 위치 사이의 거리를 구하는 함수

    public Map<String,Integer> countInRange(double userLat,double userLng){
        Map<String,Integer> result=new HashMap<>();
        int cctvCount=0; int streetLightCount=0;
        result.put("cctv",cctvCount); result.put("streetLight",streetLightCount);
        return result;
    }// 유저가 찍은 지점을 기준으로 반경 500m안에 cctv, 가로동의 개수를 반환하는 함수
}
