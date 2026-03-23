package com.dinotoptrumps.auth.adapters.in.dto;

import com.dinotoptrumps.auth.domain.model.RankTier;
import com.dinotoptrumps.auth.domain.model.UserProfile;

import java.util.UUID;

public record ProfileResponse(
        String username,
        String displayName,
        String avatarUrl,
        String bio,
        UUID favouriteCardId,
        String role,
        int leaguePoints,
        int gamesPlayed,
        int gamesWon,
        RankTier rankTier
) {
    public static ProfileResponse from(UserProfile profile) {
        return new ProfileResponse(
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                profile.getBio(),
                profile.getFavouriteCardId(),
                profile.getRole(),
                RankTier.calculateLeaguePoints(profile.getEloRating()),
                profile.getGamesPlayed(),
                profile.getGamesWon(),
                RankTier.fromElo(profile.getEloRating())
        );
    }
}
