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
                // 1. 사용자가 보낸 정보
                .imageUrl(this.imageUrl)
                .userEntity(UserEntity.builder().userId(this.userId).build())
                .contentsEntity(ContentsEntity.builder().contentId(this.contentId).build())

                // 2. 서버에서 자동으로 넣어주는 정보 (고정값)
                .status("대기중") // 처음 등록 시엔 무조건 "대기중"

                // 3. 등록 시점엔 없어야 하는 정보 (명시적으로 제외)
                .proofId(null)     // DB auto_increment 사용 시 null
                .adminEntity(null) // 아직 검토 전이므로 null
                .rejectReason(null)
                .reviewedAt(null)
                .build();
    }

//    public UserproofEntity toEntity() {
//        return UserproofEntity.builder()
//                .proofId(this.proofId)
//                .imageUrl(this.imageUrl)
//                .status(this.status)
//                .rejectReason(this.rejectReason)
//                .reviewedAt(this.getReviewedAt())
//                .userEntity(UserEntity.builder().userId(this.userId).build())
//                .contentsEntity(ContentsEntity.builder().contentId(this.contentId).build())
//                .adminEntity(this.adminId != null ? UserEntity.builder().userId(this.adminId).build() : null)
//                .build();
//    }

}
