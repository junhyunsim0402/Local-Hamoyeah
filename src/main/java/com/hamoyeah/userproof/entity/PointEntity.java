package com.hamoyeah.userproof.entity;

import com.hamoyeah.user.entity.UserEntity;
import com.hamoyeah.userproof.dto.PointDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="point_log")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PointEntity extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Integer pointId;

    @ManyToOne
    @JoinColumn(name = "proof_id", nullable = false)
    private UserproofEntity userproofEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "paid_point")
    private Integer paidPoint;


    public PointDto toDto() {
        return PointDto.builder()
                .pointId(this.pointId)
                .paidPoint(this.paidPoint)
                .paidAt(this.getCreatedAt())
                .userId(this.userEntity.getUserId())
                .proofId(this.userproofEntity.getProofId())
                .build();
    }

}
