package com.dinotoptrumps.auth.adapters.out.media;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dinotoptrumps.auth.ports.out.ForStoringMedia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class CloudinaryAdapter implements ForStoringMedia {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryAdapter.class);

    private final Cloudinary cloudinary;
    private final boolean disabled;

    public CloudinaryAdapter(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.disabled = "disabled".equals(apiKey);
        if (this.disabled) {
            this.cloudinary = null;
            log.info("Cloudinary is disabled - avatar uploads will be skipped");
        } else {
            this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret,
                    "secure", true
            ));
        }
    }

    @Override
    public String uploadAvatar(UUID userId, byte[] imageData, String contentType) {
        if (disabled) {
            log.info("Cloudinary disabled - skipping avatar upload for user {}", userId);
            return "";
        }
        try {
            Map<?, ?> result = cloudinary.uploader().upload(imageData, ObjectUtils.asMap(
                    "public_id", "avatars/" + userId,
                    "folder", "avatars",
                    "overwrite", true,
                    "resource_type", "image"
            ));
            String secureUrl = (String) result.get("secure_url");
            log.info("Avatar uploaded for user {}: {}", userId, secureUrl);
            return secureUrl;
        } catch (IOException e) {
            log.error("Failed to upload avatar for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Avatar upload failed", e);
        }
    }
}
