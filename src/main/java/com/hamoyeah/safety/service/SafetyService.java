package com.hamoyeah.safety.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hamoyeah.safety.dto.SafetyRequestDto;
import com.hamoyeah.safety.dto.SafetyResponseDto;
import com.hamoyeah.safety.entity.AirStation;
import com.hamoyeah.safety.entity.AirpollutionEntity;
import com.hamoyeah.safety.entity.CctvEntity;
import com.hamoyeah.safety.entity.StreetLampEntity;
import com.hamoyeah.safety.repository.AirpollutionRepository;
import com.hamoyeah.safety.repository.CctvRepository;
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
    private final ObjectMapper xmlMapper = new XmlMapper();
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
            .build();
    @Value("${api.service.key}")
    private String serviceKey;
    @Value("${api.service.key2}")
    private String serviceKey2;


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