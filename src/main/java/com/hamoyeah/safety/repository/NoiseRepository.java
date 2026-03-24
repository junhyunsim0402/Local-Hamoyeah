package com.hamoyeah.safety.repository;

import com.hamoyeah.safety.entity.NoiseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoiseRepository extends JpaRepository<NoiseEntity, Long> {
}
