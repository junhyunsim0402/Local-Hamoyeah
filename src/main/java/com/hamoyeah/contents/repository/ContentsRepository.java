package com.hamoyeah.contents.repository;

import com.hamoyeah.contents.Entity.ContentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentsRepository extends JpaRepository<ContentsEntity, Integer> {
    boolean existsByContentTitle(String contentTitle);
}
