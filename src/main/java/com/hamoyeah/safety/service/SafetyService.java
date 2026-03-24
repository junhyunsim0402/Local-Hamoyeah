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

        return null;
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

