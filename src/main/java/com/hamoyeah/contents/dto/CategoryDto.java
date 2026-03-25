package com.hamoyeah.contents.dto;

import com.hamoyeah.contents.Entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CategoryDto {
    private Integer categoryId;
    private Integer contentType;
    private Integer contentCategory;

    public CategoryEntity toEntity() {
        return CategoryEntity.builder()
                .contentType(this.contentType)
                .contentCategory(this.contentCategory)
                .build();
    }
}
