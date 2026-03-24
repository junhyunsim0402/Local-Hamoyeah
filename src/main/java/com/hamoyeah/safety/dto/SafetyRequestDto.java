package com.hamoyeah.safety.dto;

import lombok.Data;

@Data
public class SafetyRequestDto {
    private double lat;      // 사용자 위도
    private double lng;      // 사용자 경도
    private int radius;      // 반경 (미터)
}