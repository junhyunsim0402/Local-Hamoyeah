package com.hamoyeah.contents.repository;

import com.hamoyeah.contents.Entity.ContentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentsRepository extends JpaRepository<ContentsEntity, Integer> {
    boolean existsByContentTitle(String contentTitle);
    List<ContentsEntity> findByCategoryCategoryId(Integer categoryId);
}
