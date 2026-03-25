package com.hamoyeah.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

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
            System.out.println("주소가 비어있어 변환을 스킵");
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
                System.out.println("좌표변환 성공 주소 : "+address+" -> 위도 : "+lat+" , 경도 : "+lon);
            }else {
                System.out.println("결과 없음 해당 주소의 좌표를 찾을 수 없음 "+address);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
