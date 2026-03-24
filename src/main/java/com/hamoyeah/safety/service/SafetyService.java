package com.hamoyeah.safety.service;

import com.hamoyeah.safety.dto.SafetyRequestDto;
import com.hamoyeah.safety.dto.SafetyResponseDto;
import com.hamoyeah.util.DistanceCalculator;
import com.hamoyeah.util.GradeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class SafetyService {
    private final DistanceCalculator distanceCalculator;
    private final GradeCalculator gradeCalculator;
    public SafetyResponseDto getSafety(SafetyRequestDto requestDto){

        return null;
    }
}
