package com.dinotoptrumps.game.adapters.in.dto;

import java.util.UUID;

public record LeaderboardEntry(
        UUID userId,
        String username,
        String displayName,
        int eloRating,
        int gamesPlayed,
        int gamesWon
) {}
