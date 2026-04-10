package com.hamoyeah.userproof.dto;

import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.userproof.entity.PointEntity;
import com.hamoyeah.userproof.entity.UserproofEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PointDto {
    private Integer pointId;
    private Integer proofId;
    private Integer userId;
    private Integer paidPoint;
    private LocalDateTime paidAt;

    public PointEntity toEntity(UserEntity user, UserproofEntity userproof) {
        return PointEntity.builder()
                .userEntity(user)
                .userproofEntity(userproof)
                .paidPoint(this.paidPoint)
                .build();
    }
}
