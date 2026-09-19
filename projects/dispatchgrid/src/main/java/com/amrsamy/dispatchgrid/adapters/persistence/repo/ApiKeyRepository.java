package com.amrsamy.dispatchgrid.adapters.persistence.repo;

import com.amrsamy.dispatchgrid.adapters.persistence.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {
    Optional<ApiKeyEntity> findByKeyValueAndActiveTrue(String keyValue);

    List<ApiKeyEntity> findByTenantId(Long tenantId);
}
