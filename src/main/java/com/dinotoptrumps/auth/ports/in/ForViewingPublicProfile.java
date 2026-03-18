package com.dinotoptrumps.auth.ports.in;

import com.dinotoptrumps.auth.domain.model.UserProfile;

import java.util.UUID;

public interface ForViewingPublicProfile {
    UserProfile getPublicProfile(UUID userId);
}
