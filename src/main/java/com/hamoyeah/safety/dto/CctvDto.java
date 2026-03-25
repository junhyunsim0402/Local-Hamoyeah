package com.hamoyeah.safety.dto;

import com.hamoyeah.safety.entity.CctvEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CctvDto {
    private Long cctvId;
    private Double latitude;
    private Double longitude;
    private Integer installCnt;

    public CctvEntity toEntity(){
        return CctvEntity.builder()
                .longitude(this.longitude)
                .latitude(this.latitude)
                .installCnt(this.installCnt)
                .build();
    }
}
