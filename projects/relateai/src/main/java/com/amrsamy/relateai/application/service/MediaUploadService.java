package com.amrsamy.relateai.application.service;

import com.amrsamy.relateai.adapters.metrics.MetricsCollector;
import com.amrsamy.relateai.adapters.persistence.entity.MediaAssetEntity;
import com.amrsamy.relateai.adapters.persistence.repo.MediaAssetRepository;
import com.amrsamy.relateai.application.port.BlobStoragePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MediaUploadService {

    private final BlobStoragePort blobStorage;
    private final MediaAssetRepository mediaAssetRepository;
    private final MetricsCollector metrics;

    public MediaUploadService(
            BlobStoragePort blobStorage,
            MediaAssetRepository mediaAssetRepository,
            MetricsCollector metrics) {
        this.blobStorage = blobStorage;
        this.mediaAssetRepository = mediaAssetRepository;
        this.metrics = metrics;
    }

    @Transactional
    public MediaAssetEntity upload(Long uploaderId, MultipartFile file) throws IOException {
        BlobStoragePort.StoredBlob stored = blobStorage.store(file);
        MediaAssetEntity asset = new MediaAssetEntity();
        asset.setUploaderId(uploaderId);
        asset.setStorageKey(stored.storageKey());
        asset.setContentType(stored.contentType());
        asset.setSizeBytes(stored.sizeBytes());
        asset.setPublicPath(stored.publicPath());
        MediaAssetEntity saved = mediaAssetRepository.save(asset);
        metrics.incUploads();
        return saved;
    }
}
