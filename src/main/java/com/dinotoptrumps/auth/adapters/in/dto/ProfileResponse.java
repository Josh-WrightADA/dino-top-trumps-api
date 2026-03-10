package com.dinotoptrumps.auth.adapters.in.dto;

import com.dinotoptrumps.auth.domain.model.UserProfile;

public record ProfileResponse(
        String username,
        String displayName,
        int eloRating,
        int gamesPlayed,
        int gamesWon
) {
    public static ProfileResponse from(UserProfile profile) {
        return new ProfileResponse(
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getEloRating(),
                profile.getGamesPlayed(),
                profile.getGamesWon()
        );
    }
}
