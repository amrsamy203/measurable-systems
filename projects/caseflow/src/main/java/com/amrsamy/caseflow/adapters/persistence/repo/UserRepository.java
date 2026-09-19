package com.amrsamy.caseflow.adapters.persistence.repo;

import com.amrsamy.caseflow.adapters.persistence.entity.UserEntity;
import com.amrsamy.caseflow.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    List<UserEntity> findByTenantIdAndRoleAndActiveTrue(Long tenantId, Role role);

    List<UserEntity> findByTenantIdAndActiveTrue(Long tenantId);

    long countByTenantIdAndRoleAndActiveTrue(Long tenantId, Role role);
}
