package com.hamoyeah.contents.dto;

import com.hamoyeah.contents.Entity.CategoryEntity;
import com.hamoyeah.contents.Entity.ContentsEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentsDto {
    private Integer contentId;      // 조회할 때 사용 (저장 시에는 null)
    private Integer categoryId;     // 저장/수정 시 Category 연결용
    private Integer contentType;    // 조회 시 대분류 표시용
    private Integer contentCategory; // 조회 시 중분류 표시용
    private String contentTitle;
    private String contentDes;
    private LocalDate startDate;
    private LocalDate endDate;
    private String address;
    private Double latitude;
    private Double longitude;

    public ContentsEntity toEntity() {
        return ContentsEntity.builder()
                .contentTitle(this.contentTitle)
                .contentDes(this.contentDes)
                .address(this.address)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .category(CategoryEntity.builder().categoryId(this.categoryId).build())
                .build();
    }
}
