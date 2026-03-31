package com.hamoyeah.userproof.controller;

import com.hamoyeah.user.service.UserService;
import com.hamoyeah.userproof.dto.UserProofDto;
import com.hamoyeah.userproof.service.UserproofService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userproof")
public class UserproofController {
    private final UserproofService userproofService;
    private final UserService userService;

    // 유저 사진 등록 기능
    @PostMapping("/verify")
    public ResponseEntity<String> signup(
            @RequestHeader(value="Authorization", required = false) String bearerToken,
            @RequestBody UserProofDto userProofDto) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("토큰이 없거나 형식이 잘못되었습니다.");}
            String token = bearerToken.substring(7);
            String email = userService.getClaim(token);
            if (email == null){
                return ResponseEntity.status(401).body("유효하지 않은 토큰입니다.");}
            userproofService.signup(email, userProofDto);
            return ResponseEntity.ok("인증 등록하였습니다.");
        }
}