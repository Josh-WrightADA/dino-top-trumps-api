package com.dinotoptrumps.auth.ports.in;

import com.dinotoptrumps.auth.domain.model.User;

import java.util.UUID;

public interface ForManagingProfile {
    User getProfile(UUID userId);
    User updateDisplayName(UUID userId, String displayName);
    void changePassword(UUID userId, String currentPassword, String newPassword);
    void deleteAccount(UUID userId);
}
