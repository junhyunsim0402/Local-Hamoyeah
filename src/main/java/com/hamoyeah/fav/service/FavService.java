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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if(optionalUser.isEmpty()){return null;}

        UserEntity user=optionalUser.get();

        if (favDto.getContentId() != null) {
            // 이미 해당 유저가 이 컨텐츠를 즐겨찾기 했는지 확인
            Optional<FavEntity> existing = favRepository.findByUserEntity_EmailAndContentsEntity_ContentId(email, favDto.getContentId());
            if (existing.isPresent()) return existing.get(); // 이미 있으면 그냥 반환
        }

        if (favDto.getShopId() != null) {
            // 이미 해당 유저가 이 상점을 즐겨찾기 했는지 확인
            Optional<FavEntity> existing = favRepository.findByUserEntity_EmailAndShopEntity_ShopId(email, favDto.getShopId());
            if (existing.isPresent()) return existing.get(); // 이미 있으면 그냥 반환
        }

        ContentsEntity content=null;
        ShopEntity shop=null;

        if(favDto.getContentId()!=null){
            Optional<ContentsEntity> optionalContents=contentsRepository.findById(favDto.getContentId());
            if (optionalContents.isEmpty()){
                return null;
            } content=optionalContents.get();
        }
        if(favDto.getShopId()!=null){
            Optional<ShopEntity> optionalShop=shopRepository.findById(favDto.getShopId());
            if(optionalShop.isEmpty()){
                return null;
            } shop=optionalShop.get();
        }
        if(content==null&&shop==null){
            return null;
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

    // 즐겨찾기 갯수
    public Integer favCount(FavDto favDto){
        if(favDto.getContentId()!=null){
            return favRepository.countByContentsEntity_ContentId(favDto.getContentId());
        }
        if(favDto.getShopId()!=null){
            return favRepository.countByShopEntity_ShopId(favDto.getShopId());
        }
        return 0;
    }

    // 사용자가 즐거찾기한 것들 가져오기
    public List<FavDto> getUserFavorites(String email) {
        return favRepository.findByUserEntity_Email(email)
                .stream()
                .map(FavEntity::toDto)
                .collect(Collectors.toList());
    }
}
