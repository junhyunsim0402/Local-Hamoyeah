package com.hamoyeah.fav.service;

import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.contents.Entity.ShopEntity;
import com.hamoyeah.contents.repository.ContentsRepository;
import com.hamoyeah.contents.repository.ShopRepository;
import com.hamoyeah.fav.dto.FavDto;
import com.hamoyeah.fav.entity.FavEntity;
import com.hamoyeah.fav.repository.FavRepository;
import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavService {
    private final FavRepository favRepository;
    private final UserRepository userRepository;
    private final ContentsRepository contentsRepository;
    private final ShopRepository shopRepository;

    // 즐겨찾기 등록
    public FavEntity register(String email, FavDto favDto){
        Optional<UserEntity> optionalUser=userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            return null;
        }
        UserEntity user=optionalUser.get();
        ContentsEntity content=null;
        ShopEntity shop=null;
        if(favDto.getContentId()!=null){
            Optional<ContentsEntity> optionalContents=contentsRepository.findById(favDto.getContentId());
            if (optionalContents.isPresent()){
                content=optionalContents.get();
            }
        }
        if(favDto.getShopId()!=null){
            Optional<ShopEntity> optionalShop=shopRepository.findById(favDto.getShopId());
            if(optionalShop.isPresent()){
                shop=optionalShop.get();
            }
        }
        FavEntity entity= FavEntity.builder()
                .userEntity(user)
                .contentsEntity(content)
                .shopEntity(shop)
                .build();
        return favRepository.save(entity);
    }

    // 즐겨찾기 삭제
    @Transactional
    public FavEntity delete(String email, Integer favId){
        Optional<FavEntity> optionalFav=favRepository.findById(favId);
        if(optionalFav.isPresent()){
            FavEntity fav=optionalFav.get();
            if(fav.getUserEntity().getEmail().equals(email)){
                favRepository.delete(fav);
                return fav;
            } else{return null;}
        } else{return null;}
    }
}
