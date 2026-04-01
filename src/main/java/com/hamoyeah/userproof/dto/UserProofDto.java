package com.hamoyeah.userproof.dto;

import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.userproof.entity.UserproofEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@AllArgsConstructor @NoArgsConstructor @Builder @Data
public class UserProofDto {
    private String imageUrl;
    private Integer contentId;
    private MultipartFile uploadimg;

    private Integer proofId;
    private Integer userId;
    private String status;
    private LocalDateTime createdAt;
    private Integer adminId;
    private String rejectReason;
    private LocalDateTime reviewedAt;

    public UserproofEntity toEntity(UserEntity user, ContentsEntity content) {
        return UserproofEntity.builder()
                .imageUrl(this.imageUrl)
                .userEntity(user)
                .contentsEntity(content)
                .status("대기중") // 처음은 고정
                .build();
    }
}