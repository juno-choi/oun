package com.simol.ouncommon.health.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simol.ouncommon.health.entity.HealthSetEntity;

public interface HealthSetRepository extends JpaRepository<HealthSetEntity, Long> {
    
}
