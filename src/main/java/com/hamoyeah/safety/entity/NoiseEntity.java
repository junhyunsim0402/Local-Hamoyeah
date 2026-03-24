package com.hamoyeah.safety.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "noise")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NoiseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noiseId;
    private String address;
    private Double dayAvg;       // 주간 평균 (9시~20시 수치들의 평균)
    private Double nightAvg;     // 야간 평균 (23시~1시 수치들의 평균)
    private String areaType;
    // 위치 정보 (주소-좌표 변환기로 채울 예정)
    private Double latitude;
    private Double longitude;

}
