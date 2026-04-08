package com.hamoyeah.userproof.repository;

import com.hamoyeah.userproof.entity.UserproofEntity;
import jakarta.persistence.ManyToOne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserproofRepository extends JpaRepository<UserproofEntity, Integer > {
    List<UserproofEntity> findAllByStatus(String status);
    List<UserproofEntity> findByUserEntity_UserIdAndStatus(Integer userId, String status);
    List<UserproofEntity> findByUserEntity_Email(String email);
    @Query("SELECT p FROM UserproofEntity p " +
            "JOIN FETCH p.userEntity " +
            "LEFT JOIN FETCH p.contentsEntity " +
            "LEFT JOIN FETCH p.shopEntity " +
            "ORDER BY p.createdAt DESC")
    List<UserproofEntity> findAllWithDetails();
}
