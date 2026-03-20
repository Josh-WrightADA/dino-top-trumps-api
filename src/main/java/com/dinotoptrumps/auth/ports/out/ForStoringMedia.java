package com.dinotoptrumps.auth.ports.out;

import java.util.UUID;

public interface ForStoringMedia {
    String uploadAvatar(UUID userId, byte[] imageData, String contentType);
}
