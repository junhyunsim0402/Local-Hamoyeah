package com.hamoyeah.userproof.service;

import com.hamoyeah.user.repository.UserRepository;
import com.hamoyeah.userproof.dto.UserProofDto;
import com.hamoyeah.userproof.entity.UserproofEntity;
import com.hamoyeah.userproof.repository.UserproofRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserproofService {
    private final UserproofRepository userproofRepository;
    private final UserRepository userRepository;

    // 인증 등록

    @Transactional
    public UserproofEntity signup(UserProofDto userProofDto) {
        // DTO를 엔티티로 변환 (이때 "대기중" 등의 값이 자동으로 채워짐)
        UserproofEntity entity = userProofDto.toEntity();

        // DB 저장
        return userproofRepository.save(entity);
    }

    //    유저 정보 조회(관리자만 가능)-2차 userproof (여기서 이제 승인으로 바꿔야됨)
//    public UserDto userinfo(String loginEmail){
//        Optional<UserEntity> entityOptional=userRepository.findByEmail(loginEmail);
//        if(entityOptional.isPresent()){
//            return entityOptional.get().toDto();
//        }
//        return null;
//    }
}
