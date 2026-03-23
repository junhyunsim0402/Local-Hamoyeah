package com.hamoyeah.contents.Entity;

import com.hamoyeah.contents.dto.CategoryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "content_type", nullable = false)
    private Integer contentType;

    @Column(name = "content_category", nullable = false)
    private Integer contentCategory;

    public CategoryDto toDto() {
        return CategoryDto.builder()
                .categoryId(this.categoryId)
                .contentType(this.contentType)
                .contentCategory(this.contentCategory)
                .build();
    }
}
