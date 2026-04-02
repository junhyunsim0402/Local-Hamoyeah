package com.hamoyeah.userproof.repository;

import com.hamoyeah.userproof.entity.UserproofEntity;
import jakarta.persistence.ManyToOne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserproofRepository extends JpaRepository<UserproofEntity, Integer > {
    List<UserproofEntity> findAllByStatus(String status);
    List<UserproofEntity> findByUserEntity_UserIdAndStatus(Integer userId, String status);
}
