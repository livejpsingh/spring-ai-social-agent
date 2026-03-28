package com.socialmediaagent.repository;

import com.socialmediaagent.domain.model.BrandGuideline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandGuidelineRepository extends JpaRepository<BrandGuideline, Long> {

    Optional<BrandGuideline> findByUserId(Long userId);
}
