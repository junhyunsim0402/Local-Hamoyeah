package com.hamoyeah.safety.dto;


import com.hamoyeah.safety.entity.AirpollutionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AirpollutionDto {
    private Long airId;
    private String address;
    private LocalDateTime measuredAt;
    private Integer pm10Value;   // 미세먼지(PM10)
    private Integer pm25Value;   // 초미세먼지(PM2.5)
    private Double latitude;     // 위도
    private Double longitude;    // 경도

    public AirpollutionEntity toEntity() {
        return AirpollutionEntity.builder()
                .measuredAt(this.measuredAt)
                .address(this.address)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .pm10Value(this.pm10Value)
                .pm25Value(this.pm25Value)
                .build();
    }
}
