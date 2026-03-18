package com.dinotoptrumps.game.adapters.in.dto;

import com.dinotoptrumps.auth.domain.model.RankTier;

import java.util.UUID;

public record LeaderboardEntry(
        UUID userId,
        String username,
        String displayName,
        int eloRating,
        int gamesPlayed,
        int gamesWon,
        RankTier rankTier
) {}
