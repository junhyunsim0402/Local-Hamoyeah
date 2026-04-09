package com.hamoyeah.user.controller;

import com.hamoyeah.user.dto.LoginDto;
import com.hamoyeah.user.dto.UserDto;
import com.hamoyeah.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserService userService;
    // 유저 등록(이메일, 비밀번호, 닉네임, 관리자여부)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.signup(userDto));
    }

    // 유저 로그인(이메일, 비밀번호)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        String token = userService.login(loginDto);
        if (token != null) {
            return ResponseEntity.ok("Bearer "+token);
        } else {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 틀립니다.");
        }
    }


    // 마이페이지
    @GetMapping("/myinfo")
    public ResponseEntity<?> myinfo(@RequestHeader("Authorization") String token ){
        // @RequestHeader : http 요청의 header 정보 매핑
        // @RequestHeader("Authorization") String token 매개변수로 받는다
        //  만약에 헤더가 없거나 토큰이 없으면 비로그인
        if (token == null || !token.startsWith("Bearer")){
            return ResponseEntity.ok(false);
        }
        // 토큰 추출
        token =  token.replace("Bearer ", ""); // Bearer 없애기 띄어쓰기 주의

        String email = userService.getClaim(token);
        if (email == null) return ResponseEntity.ok(false);

        return ResponseEntity.ok(userService.myinfo(email));
    }
}
