package com.hamoyeah.safety.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "streetlamp")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StreetLampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lampId;
    @Column(nullable = false)
    private Double latitude;
    @Column(nullable = false)
    private Double longitude;
    @Column(nullable = false)
    private String address;
}
