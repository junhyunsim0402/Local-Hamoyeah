package com.hamoyeah.fav.repository;

import com.hamoyeah.fav.entity.FavEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FavRepository extends JpaRepository<FavEntity, Integer> {
    Integer countByContentsEntity_ContentId(Integer contentId);
    Integer countByShopEntity_ShopId(Integer shopId);
}
