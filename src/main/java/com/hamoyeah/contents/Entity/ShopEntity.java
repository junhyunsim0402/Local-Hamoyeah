package com.hamoyeah.contents.Entity;

import com.hamoyeah.util.주소좌표변환.LocationEntity;
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
@Table(name = "shop")
public class ShopEntity implements LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long shopId;

    @Column(nullable = false)
    private String name;        // 가맹점명

    @Column(name = "raw_category")
    private String rawCategory; // 엑셀 원본 업종 (상세 정보용)

    @Enumerated(EnumType.STRING)
    private ShopCategory shopCategory; // 분류된 업종 (마커 아이콘용)

    @Column(nullable = false)
    private String address;     // 도로명 주소

    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double latitude;

    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double longitude;

    @Override
    public void updateLocation(Double lat, Double lon){
        this.latitude = lat;
        this.longitude = lon;
    }
}
