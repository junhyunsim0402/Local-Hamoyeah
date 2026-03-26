package com.hamoyeah.user.controller;

import com.hamoyeah.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {
    private final EmailService emailService;

    // talend example: /email/send?email=test@gmail.com
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestParam String email){
        emailService.sendAuthCode(email);
        Map<String, Object> result=new HashMap<>();
        result.put("success", true);
        result.put("message", "인증번호가 이메일로 전송되었습니다.");
        return ResponseEntity.ok(result);
    }

    // talend example: /email/verify?email=test@gmail.com&code=123456
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String email,@RequestParam String code){
        boolean isVerified= emailService.verifyAuthCode(email,code);
        Map<String, Object> result=new HashMap<>();
        if(isVerified){
            result.put("success", true);
            result.put("message", "인증에 성공하였습니다.");
            return ResponseEntity.ok(result);
        } else{
            result.put("success", false);
            result.put("message", "인증번호가 일치하지 않거나 만료되었습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
}
