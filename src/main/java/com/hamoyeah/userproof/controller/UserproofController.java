package com.hamoyeah.userproof.controller;

import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.user.repository.UserRepository;
import com.hamoyeah.user.service.UserService;
import com.hamoyeah.userproof.dto.UserProofDto;
import com.hamoyeah.userproof.service.UserproofService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/userproof")
public class UserproofController {
    private final UserproofService userproofService;
    private final UserService userService;
    private final UserRepository userRepository;

    // 유저 사진 등록 기능
    @PostMapping("/verify")
    public ResponseEntity<String> verify(
            @RequestHeader(value="Authorization", required=false) String bearerToken,
            @RequestBody UserProofDto userProofDto){
        if(bearerToken==null||!bearerToken.startsWith("Bearer ")){
            return ResponseEntity.status(500).body("토큰이 없거나 형식이 잘못되었습니다.");
        }
        String token=bearerToken.substring(7);
        String email=userService.getClaim(token);
        if(email==null){
            return ResponseEntity.status(500).body("유효하지 않은 토큰입니다.");
        }
        userproofService.verify(email, userProofDto);
        return ResponseEntity.ok("인증 등록하였습니다.");
    }

    // 관리자 승인/반려 기능
    @PostMapping("/status")
    public ResponseEntity<?> status(@RequestHeader("Authorization") String token,@RequestBody UserProofDto userProofDto) {

        try {
            if(token==null||!token.startsWith("Bearer ")){
                return ResponseEntity.status(500).body("토큰이 유효하지 않습니다.");
            }
            String statusToken=token.substring(7);
            String email=userService.getClaim(statusToken);
            if(email==null){
                return ResponseEntity.status(500).body("인증 정보가 만료되었습니다.");
            }
            UserEntity user=userRepository.findByEmail(email)
                    .orElseThrow(()-> new RuntimeException("사용자를 찾을 수 없습니다."));
            if(!user.isAdmin()){
                return ResponseEntity.status(500).body("관리자 권한이 없습니다.");
            }
            userproofService.status(userProofDto, user.getUserId());
            return ResponseEntity.ok(userProofDto.getStatus()+" 처리가 완료되었습니다.");
        } catch(Exception e){
            return ResponseEntity.status(500).body("승인/반려 처리 중 오류 발생");
        }
    }

    // 관리자 인증한 사용자 전체 조회
    @GetMapping("/verifyuser")
    public ResponseEntity<?> verifyuser(@RequestHeader("Authorization") String token) {
        try {
            if(token==null||!token.startsWith("Bearer ")){
                return ResponseEntity.status(500).body("토큰이 유효하지 않습니다.");
            }
            String statusToken=token.substring(7);
            String email=userService.getClaim(statusToken);
            if(email==null){
                return ResponseEntity.status(500).body("인증 정보가 만료되었습니다.");
            }
            UserEntity user=userRepository.findByEmail(email)
                    .orElseThrow(()-> new RuntimeException("사용자를 찾을 수 없습니다."));
            if(!user.isAdmin()){
                return ResponseEntity.status(500).body("관리자 권한이 없습니다.");
            }
            List<UserProofDto> result=userproofService.verifyuser();
            return ResponseEntity.ok(result);
        } catch(Exception e){
            return ResponseEntity.status(500).body("사용자 전체 조회 처리 중 오류 발생");
        }
    }

    // 관리자 인증한 사용자 개별 조회
    @GetMapping("/detailuser")
    public ResponseEntity<?> detailuser(@RequestHeader("Authorization") String token, @RequestParam Integer userId) {
        try {
            if(token == null||!token.startsWith("Bearer ")) {
                return ResponseEntity.status(500).body("토큰이 유효하지 않습니다.");
            }
            String statusToken=token.substring(7);
            String email=userService.getClaim(statusToken);
            if(email==null) {
                return ResponseEntity.status(500).body("인증 정보가 만료되었습니다.");
            }
            UserEntity user=userRepository.findByEmail(email)
                    .orElseThrow(()-> new RuntimeException("사용자를 찾을 수 없습니다."));
            if(!user.isAdmin()){
                return ResponseEntity.status(500).body("관리자 권한이 없습니다.");
            }
            List<UserProofDto> result=userproofService.detailuser(userId);
            return ResponseEntity.ok(result);
        } catch(Exception e){
            return ResponseEntity.status(500).body("사용자 개별 조회 처리 중 오류 발생");
        }
    }


}