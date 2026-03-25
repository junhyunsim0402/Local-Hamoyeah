package com.hamoyeah.safety.repository;

import com.hamoyeah.safety.entity.CctvEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CctvRepository extends JpaRepository<CctvEntity, Long> {
    boolean existsByLatitudeAndLongitude(Double latitude, Double longitude);
}
