package com.dinotoptrumps.auth.ports.in;

import com.dinotoptrumps.auth.domain.model.User;

import java.util.UUID;

public interface ForManagingProfile {
    User getProfile(UUID userId);
    User updateProfile(UUID userId, String displayName, String bio, UUID favouriteCardId);
    User updateAvatar(UUID userId, String avatarUrl);
    void changePassword(UUID userId, String currentPassword, String newPassword);
    void deleteAccount(UUID userId);
}
