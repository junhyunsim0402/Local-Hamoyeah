package com.hamoyeah.contents.repository;

import com.hamoyeah.contents.Entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
}
