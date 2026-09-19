package com.amrsamy.relateai.adapters.persistence.repo;

import com.amrsamy.relateai.adapters.persistence.entity.MediaAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetRepository extends JpaRepository<MediaAssetEntity, Long> {
}
