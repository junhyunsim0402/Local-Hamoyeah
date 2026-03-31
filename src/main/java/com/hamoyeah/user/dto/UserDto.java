package com.hamoyeah.user.dto;

import com.hamoyeah.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class UserDto {
    private Integer userId;
    private String email;
    private String password;
    private String nickname;
    private Integer totalPoints;
    private Boolean isAdmin;

    public UserEntity toEntity(){
        return UserEntity.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
