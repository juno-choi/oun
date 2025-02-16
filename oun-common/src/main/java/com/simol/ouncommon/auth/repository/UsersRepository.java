package com.simol.ouncommon.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.simol.ouncommon.auth.entity.UserEntity;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

}
