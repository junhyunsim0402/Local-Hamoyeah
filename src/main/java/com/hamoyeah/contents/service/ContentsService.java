package com.hamoyeah.contents.service;

import com.hamoyeah.contents.Entity.CategoryEntity;
import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.contents.repository.CategoryRepository;
import com.hamoyeah.contents.repository.ContentsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    public void  contentApi(String baseUrl, int categoryId){

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리 번호 [" + categoryId + "]가 DB에 존재하지 않습니다."));

        String firstUri = baseUrl + "?page=1&pageunit=10";
        Map<String, Object> firstResponse = webClient.get()
                .uri(firstUri)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (firstResponse == null || !"OK".equals(firstResponse.get("status"))) {
            System.out.println("첫 페이지 호출 실패");
            return;
        }
        int totalPages = (int) firstResponse.get("page_count");

        for(int currentPage = 1; currentPage <= totalPages; currentPage++){
            String targetUri = baseUrl+"?page="+currentPage+"&pageunit=10";

            Map<String, Object> response = webClient.get()
                    .uri(targetUri)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

            if(results != null){
                for (Map<String, Object> item : results){
                    saveOneItem(item, category);
                }
            }
            System.out.println(categoryId + "번 카테고리: " + currentPage + " / " + totalPages + " 작업 중");
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
}
