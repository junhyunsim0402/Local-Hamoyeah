package com.hamoyeah.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
/*
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
        int cctvCount=5; int streetLightCount=10;    // TODO : API연결
        result.put("cctv",cctvCount); result.put("streetLight",streetLightCount);
        return result;
    }// 유저가 찍은 지점을 기준으로 반경 500m안에 cctv, 가로동의 개수를 반환하는 함수
}*/
@Component
public class DistanceCalculator {

    // 두 좌표(위도/경도) 사이의 거리를 미터(m) 단위로 계산하는 함수
    public double getDistanceInMeters(double lat1, double lng1, double lat2, double lng2) {
        double theta = lng1 - lng2;     // 두 지점의 경도 차이 계산
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +   // 두 지점의 높이
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        // 위도/경도를 라디안으로 변환 후 구면 삼각법 공식으로 거리 계산
        // 즉] (1번 높이*2번 높이) + ( 1번 수평 반지름 * 2번 수평반지름 * 두 사이 벌어진 각도 ) = (0,0,0)기준의 사잇각
        dist = Math.acos(dist);     // 아크코사인(길이->각도) 함수로 각도(라디안) 변환
        dist = rad2deg(dist);       // 라디안 -> 도(degree)로 변환
        return dist * 60 * 1.1515 * 1609.344;   // 도 -> 미터(m)로 단위 변환
    }

    private double deg2rad(double deg) { return (deg * Math.PI / 180.0); }  // 도(degree) -> 라디안(radian) 변환
    private double rad2deg(double rad) { return (rad * 180.0 / Math.PI); }  // 라디안(radian) -> 도(degree) 변환

    // 반경 안에 있는 데이터 개수를 세는 함수
    public int calculateCount(double userLat, double userLng, double radius, List<Map<String, Object>> apiData) {
        int count = 0;  // 반경 안에 있는 개수 초기화
        List<Map<String,Object>> cctvData=null;// TODO : cctvAPI불러오기
        List<Map<String,Object>> streetLampData=null;   // TODO : 가로등API가져오기
        if(cctvData!=null)apiData.addAll(cctvData);
        if(streetLampData!=null)apiData.addAll(streetLampData);
        for (Map<String, Object> item : apiData) {  // API 데이터 리스트를 하나씩 순회
            try {
                double targetLat = (double) item.get("lat");    // 데이터의 위도 꺼내기
                double targetLng = (double) item.get("lng");    // 데이터의 경도 꺼내기

                if (getDistanceInMeters(userLat, userLng, targetLat, targetLng) <= radius) {    // 사용자 위치와 데이터 위치 사이 거리가 반경 이내면
                    count++;    // 개수 증가
                }
            } catch (Exception e) { System.out.println("calculateCount()오류 발생 = " + e); }
        }
        return count;   // 반경 안에 있는 총 개수 반환
    }
}