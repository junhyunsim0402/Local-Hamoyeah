package com.hamoyeah.fav.repository;

import com.hamoyeah.fav.entity.FavEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavRepository extends JpaRepository<FavEntity, Integer> {
    Integer countByContentsEntity_ContentId(Integer contentId);
    Integer countByShopEntity_ShopId(Integer shopId);

    // 유저 이메일로 즐겨찾기 목록 찾기
    List<FavEntity> findByUserEntity_Email(String email);

    // 특정 유저가 특정 컨텐츠를 이미 즐겨찾기 했는지 확인 (중복 방지용)
    Optional<FavEntity> findByUserEntity_EmailAndContentsEntity_ContentId(String email, Integer contentId);
    Optional<FavEntity> findByUserEntity_EmailAndShopEntity_ShopId(String email, Integer shopId);
}
