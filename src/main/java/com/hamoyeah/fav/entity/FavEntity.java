package com.hamoyeah.fav.entity;

import com.hamoyeah.fav.dto.FavDto;
import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.contents.Entity.ShopEntity;
import com.hamoyeah.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "fav")
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class FavEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fav_id")
    private Integer favId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable=true)
    private ContentsEntity contentsEntity;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable=true)
    private ShopEntity shopEntity;

    public FavDto toDto(){
        return FavDto.builder()
                .favId(this.favId)
                .userId(this.userEntity!=null?this.userEntity.getUserId():null)
                .contentId(this.contentsEntity!=null?this.contentsEntity.getContentId():null)
                .shopId(this.shopEntity!=null?this.shopEntity.getShopId():null)
                .build();

    }
}
