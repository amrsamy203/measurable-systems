package com.amrsamy.dispatchgrid.adapters.persistence.repo;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
}
