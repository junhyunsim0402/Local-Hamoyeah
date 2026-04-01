package com.hamoyeah.contents.repository;

import com.hamoyeah.contents.Entity.ShopCategory;
import com.hamoyeah.contents.Entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
    List<ShopEntity> findByShopCategory(ShopCategory shopCategory); // 카테고리별로 shopEntity로 가져오기
}
