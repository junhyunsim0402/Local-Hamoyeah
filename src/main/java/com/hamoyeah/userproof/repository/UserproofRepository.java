package com.hamoyeah.userproof.repository;

import com.hamoyeah.userproof.entity.UserproofEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.stereotype.Repository;

@Repository
public interface UserproofRepository extends JpaRepository<UserproofEntity, Integer > {
}
