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
        return UserEntity.builder()
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .total_points(0) // 회원가입할 때 포인트에 대한 정보 필요 없으므로 0
                .isAdmin(this.isAdmin)
                .build();
    }
}
