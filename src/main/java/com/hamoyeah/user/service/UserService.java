package com.hamoyeah.user.service;

import com.hamoyeah.user.dto.LoginDto;
import com.hamoyeah.user.dto.UserDto;
import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    private final EmailService emailService;

    private String secret="123456789123456789123456789123456789";
    private Key secretKey= Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    // 토큰 발급
    public String createToken(String email){
        String token= Jwts.builder()
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()*1000*60*60*24)) // 24시간 임시 지정
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        return token;
    }

    // 토큰 클레임 추출
    public String getClaim(String token){
        try{
            Claims claims=Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Object object=claims.get("email");
            return (String) object;
        } catch(Exception e){System.out.println(e);}
        return null;
    }

    // 유저 등록
    public boolean signup(UserDto userDto){
        // 해당 이메일 인증 됐는지
        if(!emailService.isEmailVerified(userDto.getEmail())){
            throw new RuntimeException("이메일 인증이 필요합니다.");
        }

        // 등록 진행
        UserEntity saveEntity=userDto.toEntity();
        String secretpwd=passwordEncoder.encode(saveEntity.getPassword());
        saveEntity.setPassword(secretpwd);
        UserEntity savedEntity=userRepository.save(saveEntity);

        // 등록 성공 후 인증 상태 초기화
        emailService.clearVerification(userDto.getEmail());
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

//    유저 정보 조회(관리자만 가능)-2차 userproof
//    public UserDto userinfo(String loginEmail){
//        Optional<UserEntity> entityOptional=userRepository.findByEmail(loginEmail);
//        if(entityOptional.isPresent()){
//            return entityOptional.get().toDto();
//        }
//        return null;
//    }
}
