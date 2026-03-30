package com.hamoyeah.safety.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.contents.Entity.ShopEntity;
import com.hamoyeah.contents.repository.ContentsRepository;
import com.hamoyeah.contents.repository.ShopRepository;
import com.hamoyeah.safety.dto.SafetyRequestDto;
import com.hamoyeah.safety.dto.SafetyResponseDto;
import com.hamoyeah.safety.entity.*;
import com.hamoyeah.safety.repository.AirpollutionRepository;
import com.hamoyeah.safety.repository.CctvRepository;
import com.hamoyeah.safety.repository.NoiseRepository;
import com.hamoyeah.safety.repository.StreetLampRepository;
import com.hamoyeah.util.DistanceCalculator;
import com.hamoyeah.util.GradeCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
@Transactional
public class SafetyService {
    private final DistanceCalculator distanceCalculator;
    private final GradeCalculator gradeCalculator;
    private final CctvRepository cctvRepository;
    private final StreetLampRepository streetLampRepository;
    private final AirpollutionRepository airRepository;
    private final NoiseRepository noiseRepository;
    private final ContentsRepository contentsRepository;
    private final ShopRepository shopRepository;
    private final ObjectMapper xmlMapper = new XmlMapper();
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
            .build();
    @Value("${api.service.key}")
    private String serviceKey;
    @Value("${api.service.key2}")
    private String serviceKey2;


    public SafetyResponseDto getSafety(SafetyRequestDto requestDto) {
        // 1. CCTV API
        List<Map<String, Object>> cctvRawData = cctvRepository.findAll().stream()
                .map(cctv -> Map.<String, Object>of(
                        "lat", cctv.getLatitude(),
                        "lng", cctv.getLongitude()
                ))
                .collect(java.util.stream.Collectors.toList());

        // 2. 가로등 API
        List<Map<String, Object>> lampRawData = streetLampRepository.findAll().stream()
                .map(lamp -> Map.<String, Object>of(
                        "lat", lamp.getLatitude(),
                        "lng", lamp.getLongitude()
                ))
                .collect(java.util.stream.Collectors.toList());

        // 3. 반경 내 개수 계산 (DistanceCalculator 활용)
        int cctvCount = distanceCalculator.calculateCount(
                requestDto.getLat(), requestDto.getLng(), requestDto.getRadius(), cctvRawData);
        int lampCount = distanceCalculator.calculateCount(
                requestDto.getLat(), requestDto.getLng(), requestDto.getRadius(), lampRawData);

        // 4. Plus점수 환산 (GradeCalculator 활용)
        Map<String, Object> plusResult = gradeCalculator.calculatePlusScore(cctvCount, lampCount);
        int cctvScore = (int) plusResult.get("cctvScore");
        int streetLampScore = (int) plusResult.get("streetLampScore");

        // 5. 대기질 API
        List<AirpollutionEntity> airDataList=airRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toList()); // 대기질관련 api정보 가져오기
        // 6. 거리비례 대기질 값(미세먼지, 초미세먼지) 가져오기
        AirpollutionEntity nearAir=null;    // 기본값을 null로 고정
        double airMinDist=Double.MAX_VALUE;    // 측정소와의 거리를 아주 멀게 설정
        for(AirpollutionEntity air:airDataList){
            double airDist=distanceCalculator.getDistanceInMeters(
                    requestDto.getLat(),requestDto.getLng(),
                    air.getLatitude(),air.getLongitude()
            );  // 측정소와 유저가 찍은 곳의 거리 가져오기
            System.out.println("airDist = " + airDist);
            if(airDist<airMinDist){
                airMinDist=airDist;   // 측정소 하나씩 가져와서 거리가 짧으면 업데이트
                nearAir=air;    // 거리가 짧은 측정소를 저장
            }
        }
        System.out.println("airMinDist = " + airMinDist);
        int pm10= nearAir != null ? nearAir.getPm10Value() : 0; // 거리를 가져오면 값 저장
        int pm25= nearAir != null ? nearAir.getPm25Value() : 0;

        // 7. 소음 API
        List<NoiseEntity> noiseDataList=noiseRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toList());
        // 8. nosie 측정소와의 거리를 가져와서 값을 가져오기
        NoiseEntity nearNoise=null;
        double noiseMinDist=Double.MAX_VALUE;
        for(NoiseEntity nosie : noiseDataList){
            double nosieDist=distanceCalculator.getDistanceInMeters(
                    requestDto.getLat(),requestDto.getLng(),
                    nosie.getLatitude(),nosie.getLongitude()
            );
            System.out.println("측정된 소음 지점과의 거리 = " + nosieDist);
            if(nosieDist<noiseMinDist){
                noiseMinDist=nosieDist;
                nearNoise=nosie;
            }
        }
        // System.out.println("사용자가 찍은 경도 = " + requestDto.getLat()); // 확인
        // System.out.println("사용자가 찍은 위도 = " + requestDto.getLng());   // 확인
        // System.out.println("가장 가까운 소음 측정소 주소 = " + nearNoise.getAddress());  // 확인
        // System.out.println("측정소까지 거리 = " + noiseMinDist + "m");  // 확인
        if(noiseMinDist<=500){  // 소음 측정소 사이의 거리가 500미터 이하이면 실행
            System.out.println("측정소 dayAvg = " + nearNoise.getDayAvg());
            double nosieValue = nearNoise != null ? nearNoise.getDayAvg() : 0;

            // 8. 위험 점수 및 최종 등급 계산
            Map<String,Object> minusResult= gradeCalculator.minusScore(nosieValue,pm10,pm25);
            int noiseScore = (int) minusResult.get("noiseScore");
            int airScore = (int) minusResult.get("airScore");

            Map<String, Object> totalResult = gradeCalculator.totalGrade(cctvScore, streetLampScore, noiseScore, airScore);

            // List<ContentsEntity> allContents = contentsRepository.findAll();
            // List<Map<String,Object>> contentsList = distanceCalculator.getContents(      // 모든정보가 필요하면 쓰기
            //        requestDto.getLat(), requestDto.getLng(), 1000, allContents);

            return SafetyResponseDto.builder()
                    .grade(totalResult.get("grade").toString())
                    .totalScore((int)totalResult.get("totalScore"))
                    .cctvScore(cctvScore)
                    .cctvCount(cctvCount)
                    .streetLampScore(streetLampScore)
                    .streetLampCount(lampCount)
                    .noiseAvg(nosieValue)
                    .noiseScore(noiseScore)
                    .airScore(airScore)
                    .pm10(pm10)
                    .pm25(pm25)
                    // .contents(contentsList)  // 모든정보가 필요하면 쓰기
                    .build();
        }else{
            // TODO : 소음을 제외하여 점수 계산할 수 있도록 전달/응답받기 해주는 코드
            Map<String,Object> minusResult=gradeCalculator.noNoiseScore(pm10,pm25);
            int pm10Score=(int) minusResult.get("pm10Score");
            int pm25Score=(int) minusResult.get("pm25Score");
            Map<String,Object> totalResult=gradeCalculator.noNoiseGrade(cctvScore,streetLampScore,pm10Score,pm25Score);

            // List<ContentsEntity> allContents = contentsRepository.findAll();
            // List<Map<String,Object>> contentsList = distanceCalculator.getContents(      // 모든정보가 필요하면 쓰기
            //        requestDto.getLat(), requestDto.getLng(), 1000, allContents);

            // System.out.println("중요 = " + requestDto.getLng());   확인 완료
            // System.out.println("중요2 = " + requestDto.getLat());  확인 완료
            return SafetyResponseDto.builder()
                    .grade(totalResult.get("grade").toString())
                    .totalScore((int)totalResult.get("totalScore"))
                    .cctvScore(cctvScore)
                    .cctvCount(cctvCount)
                    .streetLampScore(streetLampScore)
                    .streetLampCount(lampCount)
                    .airScore(pm10Score+pm25Score)
                    .pm10(pm10Score)
                    .pm25(pm25Score)
                    // .contents(contentsList)  // 모든정보가 필요하면 쓰기
                    .build();
        }
    }
    public List<Map<String,Object>> getContents(SafetyRequestDto requestDto){
        System.out.println("service코드의 위도 = " + requestDto.getLat());   // 확인
        System.out.println("service코드의 경도 = " + requestDto.getLng());   // 확인
        List<ContentsEntity> allContents=contentsRepository.findAll();
        return distanceCalculator.getContents(requestDto.getLat(), requestDto.getLng(), 1000,allContents);
    }   // 사용자의 위도/경도, 반경 1000m, api정보를 getContents로 넣음

    public List<Map<String,Object>> getAuthContents(SafetyRequestDto requestDto){   // 500미터 안에 있는 컨텐츠 컨텐츠 불러오는 함수
        List<ContentsEntity> allContents=contentsRepository.findAll();
        List<ShopEntity> allShop=shopRepository.findAll();
        List<Map<String,Object>> contentsResult=distanceCalculator.getContents(requestDto.getLat(),requestDto.getLng(),50,allContents);    // 반경 50m 안에 있는 모든 컨텐츠 가져오기
        List<Map<String,Object>> shopResult=distanceCalculator.getShop(requestDto.getLat(),requestDto.getLng(),50,allShop);                // 반경 50m 안에 있는 모든 shop 가져오기
        List<Map<String,Object>> result=new ArrayList<>();
        result.addAll(contentsResult); result.addAll(shopResult);
        return result;
    }

    public void syncStreetLamp(String baseUrl) {
        int perPage = 1000; // 한번에 최대 500개

        String checkUrl = baseUrl + "?page=1&perPage=1&serviceKey=" + serviceKey2;
        Map<String, Object> check = webClient.get()
                .uri(checkUrl)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        int totalCount = Integer.parseInt(String.valueOf(check.get("totalCount")));
        int totalPages = (int) Math.ceil((double) totalCount / perPage);

        for (int page = 1; page <= totalPages; page++) {
            String targetUrl = baseUrl + "?page=" + page + "&perPage=" + perPage + "&serviceKey=" + serviceKey2;
            Map<String, Object> response = webClient.get()
                    .uri(targetUrl)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");

            for (Map<String, Object> item : dataList) {
                double lat = Double.parseDouble(String.valueOf(item.get("위도")));
                double lon = Double.parseDouble(String.valueOf(item.get("경도")));
                if (!streetLampRepository.existsByLatitudeAndLongitude(lat, lon)) {
                    StreetLampEntity lamp = StreetLampEntity.builder()
                            .latitude(lat)
                            .longitude(lon)
                            .address(String.valueOf(item.get("지번 주소")))
                            .build();
                    streetLampRepository.save(lamp);
                }
            }
            System.out.println("가로등 수집 중: " + page + " / " + totalPages);
        }
        System.out.println("가로등 수집 완료! 총 " + totalCount + "건 처리됨.");
    }
    public void syncCctv(String baseUrl) {
        int perPage = 1000;

        String checkUrl = baseUrl + "?page=1&perPage=1&serviceKey=" + serviceKey;
        Map<String, Object> check = webClient.get()
                .uri(checkUrl)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        int totalCount = Integer.parseInt(String.valueOf(check.get("totalCount")));
        int totalPages = (int) Math.ceil((double) totalCount / perPage);

        for (int page = 1; page <= totalPages; page++) {
            String targetUrl = baseUrl + "?page=" + page + "&perPage=" + perPage + "&serviceKey=" + serviceKey;
            Map<String, Object> response = webClient.get()
                    .uri(targetUrl)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");

            for (Map<String, Object> item : dataList) {
                double lat = Double.parseDouble(String.valueOf(item.get("위도")));
                double lon = Double.parseDouble(String.valueOf(item.get("경도")));

                if (!cctvRepository.existsByLatitudeAndLongitude(lat, lon)) {
                    CctvEntity cctv = CctvEntity.builder()
                            .latitude(lat)
                            .longitude(lon)
                            .installCnt(Integer.parseInt(String.valueOf(item.get("설치대수"))))
                            .build();
                    cctvRepository.save(cctv);
                }
            }
            System.out.println("CCTV 수집 중: " + page + " / " + totalPages);
        }
        System.out.println("CCTV 수집 완료! 총 " + totalCount + "건 처리됨.");
    }

    public boolean syncAir(){
        for(AirStation station : AirStation.values()){
            try{
                String xmlRaw = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .host("apis.data.go.kr")
                                .path("B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty")
                                .queryParam("serviceKey", serviceKey)
                                .queryParam("returnType", "xml")
                                .queryParam("stationName", station.getName())
                                .queryParam("dataTerm", "daily")
                                .queryParam("ver", "1.3")
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JsonNode root = xmlMapper.readTree(xmlRaw);
                JsonNode items = root.path("body").path("items").path("item");

                if (items.isArray() && items.has(0)) {
                    JsonNode latestItem = items.get(0); // 가장 최근 시간 데이터

                    String dataTimeStr = latestItem.path("dataTime").asText();
                    LocalDateTime measuredAt = LocalDateTime.parse(dataTimeStr,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                    // 4. 중복 체크 후 저장 (Enum의 주소와 좌표 활용!)
                    if (!airRepository.existsByAddressAndMeasuredAt(station.getAddress(), measuredAt)) {
                        AirpollutionEntity entity = AirpollutionEntity.builder()
                                .address(station.getAddress())
                                .latitude(station.getLat())
                                .longitude(station.getLon())
                                .pm10Value(Integer.valueOf(latestItem.path("pm10Value").asText()))
                                .pm25Value(Integer.valueOf(latestItem.path("pm25Value").asText()))
                                .measuredAt(measuredAt)
                                .build();

                        airRepository.save(entity);
                    }
                }
            }catch (Exception e){
                System.out.println(e);
                return false;
            }
        }
        return true;
    }




}