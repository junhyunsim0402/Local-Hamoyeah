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

@Service
@RequiredArgsConstructor
public class FavService {
    private final FavRepository favRepository;
    private final UserRepository userRepository;
    private final ContentsRepository contentsRepository;
    private final ShopRepository shopRepository;

    // 즐겨찾기 등록
    public FavEntity register(String email, FavDto favDto){
        UserEntity user=userRepository.findByEmail(email).get();
        ContentsEntity content=null;
        ShopEntity shop=null;
        if(favDto.getContentId()!=null){
            content=contentsRepository.findById(favDto.getContentId()).get();
        } else if(favDto.getShopId()!=null){
            shop=shopRepository.findById(favDto.getShopId()).get();
        }
        FavEntity entity= FavEntity.builder()
                .userEntity(user)
                .contentsEntity(content)
                .shopEntity(shop)
                .build();
        return favRepository.save(entity);
    }
}
