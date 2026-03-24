package com.hamoyeah.contents.service;

import com.hamoyeah.contents.Entity.CategoryEntity;
import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.contents.repository.CategoryRepository;
import com.hamoyeah.contents.repository.ContentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentsService {
    private final ContentsRepository contentsRepository;
    private final CategoryRepository categoryRepository;
    private final WebClient webClient = WebClient.builder().build();

    @Value("${api.service.key}")
    private String serviceKey;

    // 진주시 관광, 축제, 문화재 가져오는 메소드
    public void  contentApi(String baseUrl, int categoryId){ // 베이스 주소, 저장할 카테고리 id 매개변수

        CategoryEntity category = categoryRepository.findById(categoryId) // 카테고리 id가 존재하는지 찾기
                .orElseThrow(() -> new RuntimeException("카테고리 번호 [" + categoryId + "]가 DB에 존재하지 않습니다."));

        String firstUri = baseUrl + "?page=1&pageunit=10"; // 초기 url 설정 베이스 주소 + 페이지, 페이지당 칼럼 수
        Map<String, Object> firstResponse = webClient.get()
                .uri(firstUri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (firstResponse == null || !"OK".equals(firstResponse.get("status"))) { // 첫 응답이 없거나 응답 값에서 상태가 ok가 아니면
            System.out.println("첫 페이지 호출 실패");
            return;
        }
        int totalPages = (int) firstResponse.get("page_count"); // 각 api 마다 totalpage가 다르므로 첫번째 응답에서 전체 페이지 카운트를 가져옴

        for(int currentPage = 1; currentPage <= totalPages; currentPage++){ // 전체 페이지 카운트 만큼 반복
            String targetUri = baseUrl+"?page="+currentPage+"&pageunit=10";

            Map<String, Object> response = webClient.get()
                    .uri(targetUri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results"); // 결과부분 : 필요한 부분

            if(results != null){
                for (Map<String, Object> item : results){
                    saveOneItem(item, category);
                }
            }
            System.out.println(categoryId + "번 카테고리: " + currentPage + " / " + totalPages + " 작업 중");
        }
    }

    // 진주시 공공체육시설, 공공미술, 건축물 미술 가져오는 메소드
    public void dataApi(String baseUrl, int categoryId){
        String checkUrl = baseUrl + "?page=1&perPage=1&serviceKey=" + serviceKey; // 처음에 잘 가져와 지는지 확인하는 과정
        Map<String, Object> checkRes = webClient.get()
                .uri(checkUrl)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        int totalCount = Integer.parseInt(String.valueOf(checkRes.get("totalCount"))); // 전체 레코드 수 가져오기

        String finalUrl = baseUrl + "?page=1&perPage=" + totalCount + "&serviceKey="+serviceKey; // 전체 레코드를 한꺼번에 가져오기
        Map<String, Object> response = webClient.get()
                .uri(finalUrl)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data"); // data 부분이 필요한 부분이므로 가져와서 저장

        CategoryEntity category = categoryRepository.findById(categoryId).orElseThrow(); // 존재하는 카테고리 번호인지 확인

        if (dataList != null) {
            for (Map<String, Object> item : dataList) {
                String title = getTitleFromJson(item);
                String address = getAddressFromJson(item);
                String type = String.valueOf(item.getOrDefault("종류", "공공미술/작품"));

                if (!contentsRepository.existsByContentTitle(title)) {
                    ContentsEntity entity = ContentsEntity.builder()
                            .contentTitle(title)
                            .contentDes(type)
                            .address(address)
                            .latitude(0.0)
                            .longitude(0.0)
                            .category(category)
                            .build();
                    contentsRepository.save(entity);
                }
            }
        }
    }



    private void saveOneItem(Map<String, Object> item, CategoryEntity category){
        String title = String.valueOf(item.get("name"));

        if (!contentsRepository.existsByContentTitle(title)) {
            ContentsEntity entity = ContentsEntity.builder()
                    .contentTitle(title)
                    .contentDes(String.valueOf(item.get("content")))
                    .address(String.valueOf(item.get("address")))
                    .latitude(parseSafeDouble(item.get("xposition")))
                    .longitude(parseSafeDouble(item.get("yposition")))
                    .category(category)
                    .build();
            contentsRepository.save(entity);
        }
    }
    /*
     * 문자열 형태의 좌표 데이터를 안전하게 Double로 변환하는 메서드
     * 값이 없거나 이상하면 0.0을 반환해서 에러를 방지합니다.
     * 추후 주소 -> 좌표 변환 메소드 생성후 삭제 예정
     */
    private double parseSafeDouble(Object value) {
        // 1. 값이 아예 없거나 빈 칸("")인 경우 체크
        if (value == null || value.toString().trim().isEmpty() || value.toString().equals("null")) {
            return 0.0;
        }

        try {
            // 2. 숫자로 변환 시도
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            // 3. 만약 "abc" 같은 이상한 글자가 들어있어도 0.0을 줘서 서버가 안 죽게 함
            System.out.println("숫자 변환 실패: " + value);
            return 0.0;
        }
    }

    private String getTitleFromJson(Map<String, Object> item) {
        if (item.containsKey("시설명")) return String.valueOf(item.get("시설명"));
        if (item.containsKey("작품명")) return String.valueOf(item.get("작품명"));
        if (item.containsKey("공공미술 명")) return String.valueOf(item.get("공공미술 명")); // 공공미술용
        return "이름 없음";
    }

    private String getAddressFromJson(Map<String, Object> item) {
        if (item.containsKey("새주소") && item.get("새주소") != null) return String.valueOf(item.get("새주소"));
        if (item.containsKey("지번주소")) return String.valueOf(item.get("지번주소"));
        if (item.containsKey("건축물주소")) return String.valueOf(item.get("건축물주소")); // 건축물 미술용
        if (item.containsKey("보유(전시) 장소")) return String.valueOf(item.get("보유(전시) 장소")); // 공공미술용
        return "주소 없음";
    }
}
