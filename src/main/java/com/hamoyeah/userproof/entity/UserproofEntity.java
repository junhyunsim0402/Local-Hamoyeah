package com.hamoyeah.userproof.entity;

import com.hamoyeah.contents.Entity.ContentsEntity;
import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.userproof.dto.UserProofDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Table(name="userproof")
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class UserproofEntity extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proof_id")
    private Integer proofId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    private ContentsEntity contentsEntity;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = true)
    private UserEntity adminEntity;

    @Column(name = "reject_reason", length = 100)
    private String rejectReason;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    // ToDto 메서드
    public UserProofDto toDto() {
        return UserProofDto.builder()
                .proofId(this.proofId)
                .userId(this.userEntity.getUserId())
                .contentId(this.contentsEntity.getContentId())
                .imageUrl(this.imageUrl)
                .status(this.status)
                .createdAt(this.getCreatedAt())
                .adminId(this.adminEntity.getUserId())
                .rejectReason(this.rejectReason)
                .reviewedAt(this.reviewedAt)
                .build();
    }
}
