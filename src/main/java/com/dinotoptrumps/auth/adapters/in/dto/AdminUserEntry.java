package com.dinotoptrumps.auth.adapters.in.dto;

import com.dinotoptrumps.auth.domain.model.User;

import java.time.Instant;
import java.util.UUID;

public record AdminUserEntry(
        UUID id,
        String username,
        String displayName,
        String email,
        String role,
        String status,
        int eloRating,
        int gamesPlayed,
        int gamesWon,
        Instant createdAt
) {
    public static AdminUserEntry from(User user) {
        return new AdminUserEntry(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getEloRating(),
                user.getGamesPlayed(),
                user.getGamesWon(),
                user.getCreatedAt()
        );
    }
}
