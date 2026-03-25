package com.hamoyeah.safety.service;

import com.hamoyeah.safety.dto.SafetyRequestDto;
import com.hamoyeah.safety.dto.SafetyResponseDto;
import com.hamoyeah.safety.entity.CctvEntity;
import com.hamoyeah.safety.entity.StreetLampEntity;
import com.hamoyeah.safety.repository.CctvRepository;
import com.hamoyeah.safety.repository.StreetLampRepository;
import com.hamoyeah.util.DistanceCalculator;
import com.hamoyeah.util.GradeCalculator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
@Transactional
public class SafetyService {
    private final DistanceCalculator distanceCalculator;
    private final GradeCalculator gradeCalculator;
    private final CctvRepository cctvRepository;
    private final StreetLampRepository streetLampRepository;
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
                StreetLampEntity lamp = StreetLampEntity.builder()
                        .latitude(lat)
                        .longitude(lon)
                        .address(String.valueOf(item.get("지번 주소")))
                        .build();
                streetLampRepository.save(lamp);
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

                CctvEntity cctv = CctvEntity.builder()
                        .latitude(lat)
                        .longitude(lon)
                        .installCnt(Integer.parseInt(String.valueOf(item.get("설치대수"))))
                        .build();
                cctvRepository.save(cctv);
            }
            System.out.println("CCTV 수집 중: " + page + " / " + totalPages);
        }
        System.out.println("CCTV 수집 완료! 총 " + totalCount + "건 처리됨.");
    }
}