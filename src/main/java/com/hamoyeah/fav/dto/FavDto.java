package com.hamoyeah.fav.dto;

import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.contents.Entity.ShopEntity;
import com.hamoyeah.fav.entity.FavEntity;
import com.hamoyeah.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class FavDto {
    private Integer favId;
    private Integer userId;
    private Integer contentId;
    private Integer shopId;

    public FavEntity toEntity(UserEntity user, ContentsEntity content, ShopEntity shop){
        return FavEntity.builder()
                .userEntity(user)
                .contentsEntity(content)
                .shopEntity(shop)
                .build();
    }

}
