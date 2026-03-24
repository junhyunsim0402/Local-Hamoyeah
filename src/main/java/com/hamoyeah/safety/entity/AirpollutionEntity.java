package com.hamoyeah.safety.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "airpollution")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AirpollutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long airId;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDateTime measuredAt;

    private Integer pm10Value;   // 미세먼지(PM10)
    private Integer pm25Value;   // 초미세먼지(PM2.5)

    private Double latitude;     // 위도
    private Double longitude;    // 경도
}
