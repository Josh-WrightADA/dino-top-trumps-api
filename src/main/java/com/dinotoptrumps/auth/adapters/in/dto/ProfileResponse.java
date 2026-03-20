package com.dinotoptrumps.auth.adapters.in.dto;

import com.dinotoptrumps.auth.domain.model.RankTier;
import com.dinotoptrumps.auth.domain.model.UserProfile;

public record ProfileResponse(
        String username,
        String displayName,
        String avatarUrl,
        int eloRating,
        int gamesPlayed,
        int gamesWon,
        RankTier rankTier
) {
    public static ProfileResponse from(UserProfile profile) {
        return new ProfileResponse(
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                profile.getEloRating(),
                profile.getGamesPlayed(),
                profile.getGamesWon(),
                RankTier.fromElo(profile.getEloRating())
        );
    }
}
