package com.hamoyeah.util;

import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.contents.Entity.ShopEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public List<Map<String,Object>> getContents(double userLat,double userLng, double radius, List<ContentsEntity> contentsApiData){
        List<Map<String,Object>> result=new ArrayList<>();
        for(ContentsEntity item : contentsApiData) {
            try {
                if(item.getLatitude() == null || item.getLongitude() == null ||
                        item.getLatitude() == 0.0 || item.getLongitude() == 0.0) continue;  // 위도/경도 데이터가 없을시 무시
                double dist=getDistanceInMeters(userLat,userLng,item.getLatitude(),item.getLongitude());
                if(dist<=radius){
                    Map<String,Object> contents=new HashMap<>();    // 반경안에 있는 contents를 담을 Map
                    contents.put("contentsId",item.getContentId());
                    contents.put("contentsTitle",item.getContentTitle());
                    contents.put("categoryId",item.getCategory().getCategoryId());
                    contents.put("lat",item.getLatitude());
                    contents.put("lng",item.getLongitude());
                    result.add(contents);   // 컨텐츠 아이디/제목, 위도 경도 저장
                }
            } catch (Exception e) { System.out.println("e = " + e); }
        }
        return result;
    }

    public List<Map<String,Object>> getShop(double userLat,double userLng, double radius, List<ShopEntity> shopApiData){
        List<Map<String,Object>> result=new ArrayList<>();
        for(ShopEntity item : shopApiData) {
            try {
                if(item.getLatitude() == null || item.getLongitude() == null ||
                        item.getLatitude() == 0.0 || item.getLongitude() == 0.0) continue;  // 위도/경도 데이터가 없을시 무시

                double dist=getDistanceInMeters(userLat,userLng,item.getLatitude(),item.getLongitude());
                if(dist<=radius){
                    Map<String,Object> contents=new HashMap<>();    // 반경안에 있는 contents를 담을 Map
                    contents.put("shopId",item.getShopId());
                    contents.put("shopTitle",item.getName());
                    contents.put("shopCategory",item.getShopCategory() != null ?
                            item.getShopCategory().name() : "ETC"); // 없으면 ETC
                    contents.put("lat",item.getLatitude());
                    contents.put("lng",item.getLongitude());
                    result.add(contents);   // 컨텐츠 아이디/제목, 위도 경도 저장
                }
            } catch (Exception e) { System.out.println("shop 거리 계산 실패 = " + e); }
        }
        return result;
    }
}