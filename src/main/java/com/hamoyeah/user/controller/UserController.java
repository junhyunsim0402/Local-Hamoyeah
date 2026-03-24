package com.hamoyeah.user.controller;

import com.hamoyeah.user.dto.LoginDto;
import com.hamoyeah.user.dto.UserDto;
import com.hamoyeah.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hamoyeah/user")
public class UserController {
    private final UserService userService;

    // 유저 등록(이메일, 비밀번호, 닉네임 필요)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto userDto){
        return ResponseEntity.ok(userService.signup(userDto));
    }

    // 유저 로그인(이메일, 비밀번호 필요)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpSession session){
        boolean result=userService.login(loginDto);
        if(result) {
            session.setAttribute("loginEmail", loginDto.getEmail());
            return ResponseEntity.ok(true);
        } return ResponseEntity.ok(false);
    }

    // 유저 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session){
        session.removeAttribute("loginEmail");
        return ResponseEntity.ok(true);
    }

    // 유저 전체 조회 - 수정해야함 관리자, 유저 다같이 나옴
    @GetMapping("/userlist")
    public ResponseEntity<?> userlist(){
        List<UserDto> result=userService.userlist();
        return ResponseEntity.ok(result);
    }
}
