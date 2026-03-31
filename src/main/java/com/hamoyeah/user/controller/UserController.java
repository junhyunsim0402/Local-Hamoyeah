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
public class UserController {
    private final UserService userService;

    // 유저 등록(이메일, 비밀번호, 닉네임, 관리자여부)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.signup(userDto));
    }

    // 유저 로그인(이메일, 비밀번호)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        boolean result=userService.login(loginDto);
        if(result) {
            String token=userService.createToken(loginDto.getEmail());
            return ResponseEntity.ok()
                    .header("Authorization","Bearer "+token)
                    .body(true);
        } return ResponseEntity.ok(false);
    }
}
