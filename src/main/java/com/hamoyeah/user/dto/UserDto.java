package com.hamoyeah.user.dto;

import com.hamoyeah.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class UserDto {
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private Integer total_points;
    private Boolean isAdmin;

    public UserEntity toEntity(){
        boolean admincheck=(email!=null&&email.endsWith("@admin.com")); // 일단 이메일 주소가 @admin.com이면 관리자로 분류 > 근데 @admin.com은 없으므로 다시 고려...
        return UserEntity.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .total_points(0) // 회원가입할 때 포인트에 대한 정보 필요 없으므로 0
                .isAdmin(admincheck)
                .build();
    }
}
