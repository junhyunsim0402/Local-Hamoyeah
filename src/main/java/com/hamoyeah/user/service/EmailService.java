package com.hamoyeah.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private static final Map<String, String> authCodeMap=new HashMap<>();
    private static final Map<String, Boolean> verifiedEmail=new HashMap<>();

    public void sendAuthCode(String toEmail) {
        // 6자리 인증번호 생성
        String authCode = createAuthCode();

        // 메모리에 저장
        authCodeMap.put(toEmail, authCode);

        // 메일 구성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[이메일 인증] 인증번호 안내");
        message.setText("안녕하세요.\n\n" + "인증번호는[" + authCode + "] 입니다.\n" + "해당 인증번호를 입력하여 인증을 완료해주세요. \n\n" + "감사합니다.");

        // 메일 전송
        javaMailSender.send(message);
    }

    public boolean verifyAuthCode(String email, String userInputCode){
        if(authCodeMap.containsKey(email)&&authCodeMap.get(email).equals(userInputCode)){
            authCodeMap.remove(email); // 인증 성공 시 삭제
            verifiedEmail.put(email, true); // 인증 완료된 이메일 기록
            return true;
        } return false;
    }

    // 외부에서 인증 여부 확인 가능한 메소드
    public boolean isEmailVerified(String email){
        return verifiedEmail.getOrDefault(email, false);
    }

    // 회원가입 완료 후 인증 상태 삭제
    public void clearVerification(String email){
        verifiedEmail.remove(email);
    }

    // 6자리 난수 생성
    private String createAuthCode(){
        Random random=new Random();
        int number=100000+random.nextInt(900000);
        return String.valueOf(number);
    }
}
