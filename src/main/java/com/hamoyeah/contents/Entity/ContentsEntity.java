package com.hamoyeah.contents.Entity;

import com.hamoyeah.contents.dto.ContentsDto;
import com.hamoyeah.util.주소좌표변환.LocationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "contents")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ContentsEntity implements LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Integer contentId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "content_title", nullable = false, length = 100)
    private String contentTitle;

    @Column(name = "content_des", columnDefinition = "TEXT")
    private String contentDes;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    public ContentsDto toDto() {
        return ContentsDto.builder()
                .contentId(this.contentId)
                .categoryId(this.category.getCategoryId())
                .contentType(this.category.getContentType())
                .contentCategory(this.category.getContentCategory())
                .contentTitle(this.contentTitle)
                .address(this.address)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .build();
    }
    @Override
    public void updateLocation(Double lat, Double lon){
        this.latitude = lat;
        this.longitude = lon;
    }
}
