package com.amrsamy.relateai.application.port;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BlobStoragePort {

    StoredBlob store(MultipartFile file) throws IOException;

    record StoredBlob(String storageKey, String contentType, long sizeBytes, String publicPath) {}
}
