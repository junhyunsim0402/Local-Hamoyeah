package com.hamoyeah.util.주소좌표변환;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {
    @Value("${api.kakao.key}")
    private String kakaoApiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://dapi.kakao.com")
            .build();

    public void fillCoordinates(LocationEntity entity){
        String address = entity.getAddress();
        if (address == null || address.isBlank()){
            log.warn("좌표 변환 스킵: 엔티티(ID: {})의 주소 데이터가 비어있습니다.");
            return;
        }
        try {
            String jsonRaw = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    .header("Authorization", "KakaoAK "+kakaoApiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonRaw);
            JsonNode documents = root.path("documents");

            if (documents.isArray() && documents.size()>0){
                JsonNode firstResult = documents.get(0);

                double lon = firstResult.path("x").asDouble();
                double lat = firstResult.path("y").asDouble();

                entity.updateLocation(lat, lon);
            }else {
                log.warn("좌표를 찾을 수 없는 주소입니다: {}", address);
            }

        } catch (WebClientResponseException e) {
            // API 키 문제나 권한, 쿼리 제한 등 HTTP 에러 처리
            log.error("카카오 API 호출 실패(HTTP {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            // 그 외 예기치 못한 모든 에러
            log.error("좌표 변환 중 알 수 없는 시스템 오류 발생 (주소: {})", address, e);
        }
    }
}
