package com.amrsamy.relateai.adapters.storage;

import com.amrsamy.relateai.application.port.BlobStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Component
public class LocalBlobStorageAdapter implements BlobStoragePort {

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    private final Path root;

    public LocalBlobStorageAdapter(@Value("${relateai.storage.local-dir:./data/uploads}") String localDir)
            throws IOException {
        this.root = Path.of(localDir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    @Override
    public StoredBlob store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
        if (!ALLOWED.contains(contentType)) {
            throw new IllegalArgumentException("Only image uploads are allowed (jpeg/png/gif/webp)");
        }

        String extension = extensionFor(file.getOriginalFilename(), contentType);
        String key = UUID.randomUUID() + extension;
        Path target = root.resolve(key).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Invalid storage path");
        }
        Files.copy(file.getInputStream(), target);

        String publicPath = "/uploads/" + key;
        return new StoredBlob(key, contentType, file.getSize(), publicPath);
    }

    private String extensionFor(String originalName, String contentType) {
        String cleaned = StringUtils.cleanPath(originalName == null ? "" : originalName);
        int dot = cleaned.lastIndexOf('.');
        if (dot >= 0 && cleaned.length() - dot <= 5) {
            return cleaned.substring(dot).toLowerCase();
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
