package com.hamoyeah.userproof.dto;

import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.userproof.entity.UserproofEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor @NoArgsConstructor @Builder @Data
public class UserProofDto {
    private Integer proofId;
    private Integer userId;
    private Integer contentId;
    private String imageUrl;
    private String status;
    private LocalDateTime createdAt;
    private Integer adminId;
    private String rejectReason;
    private LocalDateTime reviewedAt;

    public UserproofEntity toEntity() {
        return UserproofEntity.builder()
                .proofId(this.proofId)
                .imageUrl(this.imageUrl)
                .status(this.status)
                .rejectReason(this.rejectReason)
                .reviewedAt(this.getReviewedAt())
                .userEntity(UserEntity.builder().userId(this.userId).build())
                .contentsEntity(ContentsEntity.builder().contentId(this.contentId).build())
                .adminEntity(this.adminId != null ? UserEntity.builder().userId(this.adminId).build() : null)
                .build();
    }

}
