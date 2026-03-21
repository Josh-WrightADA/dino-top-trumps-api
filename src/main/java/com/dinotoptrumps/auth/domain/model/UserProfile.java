package com.dinotoptrumps.auth.domain.model;

import java.util.UUID;

public class UserProfile {

    private final UUID userId;
    private final String username;
    private final String displayName;
    private final String avatarUrl;
    private final String bio;
    private final UUID favouriteCardId;
    private final String role;
    private final int eloRating;
    private final int gamesPlayed;
    private final int gamesWon;

    public UserProfile(UUID userId, String username, String displayName, String avatarUrl,
                       String bio, UUID favouriteCardId, String role,
                       int eloRating, int gamesPlayed, int gamesWon) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.favouriteCardId = favouriteCardId;
        this.role = role;
        this.eloRating = eloRating;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    public static UserProfile fromUser(User user) {
        return new UserProfile(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getFavouriteCardId(),
                user.getRole().name(),
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public UUID getFavouriteCardId() {
        return favouriteCardId;
    }

    public String getRole() {
        return role;
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
