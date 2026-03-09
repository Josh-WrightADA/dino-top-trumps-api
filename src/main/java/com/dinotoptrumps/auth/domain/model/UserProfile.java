package com.dinotoptrumps.auth.domain.model;

import java.util.UUID;

public class UserProfile {

    private final UUID userId;
    private final String username;
    private final String displayName;
    private final int eloRating;
    private final int gamesPlayed;
    private final int gamesWon;

    public UserProfile(UUID userId, String username, String displayName,
                       int eloRating, int gamesPlayed, int gamesWon) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.eloRating = eloRating;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    public static UserProfile fromUser(User user) {
        return new UserProfile(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEloRating(),
                user.getGamesPlayed(),
                user.getGamesWon()
        );
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getEloRating() {
        return eloRating;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }
}
