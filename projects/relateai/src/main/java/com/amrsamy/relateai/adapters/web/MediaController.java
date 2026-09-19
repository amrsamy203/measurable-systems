package com.amrsamy.relateai.adapters.web;

import com.amrsamy.relateai.adapters.persistence.entity.MediaAssetEntity;
import com.amrsamy.relateai.adapters.web.dto.ApiDtos;
import com.amrsamy.relateai.adapters.web.security.RelateAiUserPrincipal;
import com.amrsamy.relateai.application.service.MediaUploadService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaUploadService mediaUploadService;

    public MediaController(MediaUploadService mediaUploadService) {
        this.mediaUploadService = mediaUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiDtos.MediaUploadResponse> upload(
            @AuthenticationPrincipal RelateAiUserPrincipal principal,
            @RequestPart("file") MultipartFile file) throws IOException {
        MediaAssetEntity asset = mediaUploadService.upload(principal.getId(), file);
        return ResponseEntity.ok(new ApiDtos.MediaUploadResponse(
                asset.getId(),
                asset.getPublicPath(),
                asset.getContentType(),
                asset.getSizeBytes()));
    }
}
