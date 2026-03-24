package com.hamoyeah.safety.dto;


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
}
