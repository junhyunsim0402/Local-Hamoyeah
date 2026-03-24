package com.hamoyeah.user.service;

import com.hamoyeah.user.dto.LoginDto;
import com.hamoyeah.user.dto.UserDto;
import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    // 유저 등록
    public boolean signup(UserDto userDto){
        UserEntity saveEntity=userDto.toEntity();
        if(saveEntity.getEmail()!=null&&saveEntity.getEmail().endsWith("@admin.com")){
            String originalNickname=saveEntity.getNickname();
            saveEntity.setNickname(originalNickname+"(관리자)");
        }
        String secretpwd=passwordEncoder.encode(saveEntity.getPassword());
        saveEntity.setPassword(secretpwd);

        UserEntity savedEntity=userRepository.save(saveEntity);
        return savedEntity.getUserId()>0;
    }

    // 유저 로그인
    public boolean login(LoginDto loginDto){
        Optional<UserEntity> optionalUser=userRepository.findByEmail(loginDto.getEmail());
        if(optionalUser.isPresent()){
            UserEntity userEntity=optionalUser.get();
            boolean result=passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword());
            if(result==true){return true;}
            else{return false;}
        } return false;
    }

    // 유저 전체 조회 - 수정해야함 관리자, 유저 다같이 나옴
    public List<UserDto> userlist(){
        List<UserEntity> entityList=userRepository.findAll();
        List<UserDto> userDtoList=new ArrayList<>();
        for(int i=0; i< entityList.size(); i++){
            UserEntity entity=entityList.get(i); userDtoList.add(entity.toDto());
        }  return userDtoList;
    }
}
