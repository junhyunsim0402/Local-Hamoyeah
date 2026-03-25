package com.hamoyeah.safety.entity;

import com.hamoyeah.safety.dto.CctvDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cctv")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CctvEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cctvId;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    @Column(nullable = false)
    private Integer installCnt; // 설치대수

    public CctvDto toDto(){
        return CctvDto.builder()
                .cctvId(this.cctvId)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .installCnt(this.installCnt)
                .build();
    }
}
