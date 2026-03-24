package com.hamoyeah.safety.repository;

import com.hamoyeah.safety.entity.StreetLampEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreetLampRepository extends JpaRepository<StreetLampEntity, Integer> {
}
