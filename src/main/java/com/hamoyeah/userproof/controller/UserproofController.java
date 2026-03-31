package com.hamoyeah.userproof.controller;

import com.hamoyeah.userproof.dto.UserProofDto;
import com.hamoyeah.userproof.service.UserproofService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userproof")
public class UserproofController {
    private final UserproofService userproofService;

    // 유저 인증 등록
    @PostMapping("/verify")
    public ResponseEntity<?> signup(@RequestBody UserProofDto userProofDto){
        return ResponseEntity.ok(userproofService.signup(userProofDto));
    }

    //    유저 정보 조회(관리자만 가능)-2차 userproof (여기서 이제 승인으로 바꿔야됨)
//    @GetMapping("/detailinfo")
//    public ResponseEntity<?> userinfo(@RequestHeader("Authorization")String token, UserDto userDto){
//        if(token==null||!token.startsWith("Bearer ")){
//            return ResponseEntity.ok(false);
//        }
//        token=token.replace("Bearer ","");
//        String email=userService.getClaim(token);
//        UserDto user=userService.userinfo(email);
//        if(user==null || user.getIsAdmin()== null|| !user.getIsAdmin()){
//            return ResponseEntity.ok(false);
//        }
//        return ResponseEntity.ok(user);
//    }
}
