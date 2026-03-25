package com.hamoyeah.safety.repository;

import com.hamoyeah.safety.entity.AirpollutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AirpollutionRepository extends JpaRepository<AirpollutionEntity, Long> {
    boolean existsByAddressAndMeasuredAt(String address, LocalDateTime measuredAt);
}

