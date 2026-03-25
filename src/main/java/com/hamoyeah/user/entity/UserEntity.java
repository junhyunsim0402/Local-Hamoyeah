package com.hamoyeah.user.entity;

import com.hamoyeah.user.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "User")
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class UserEntity extends BaseTime{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false)
    @Builder.Default
    private int total_points=0;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAdmin=false;

    public UserDto toDto(){
        return UserDto.builder()
                .email(email)
                .nickname(nickname)
                .total_points(0)
                .isAdmin(isAdmin)
                .build();
    }
}
