package com.dinotoptrumps.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String username;
    private final String email;
    private String passwordHash;
    private String displayName;
    private String avatarUrl;
    private int eloRating;
    private int gamesPlayed;
    private int gamesWon;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UUID id, String username, String email, String passwordHash,
                String displayName, String avatarUrl, int eloRating, int gamesPlayed, int gamesWon,
                Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.eloRating = eloRating;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(String username, String email, String passwordHash) {
        Instant now = Instant.now();
        return new User(
                UUID.randomUUID(),
                username,
                email,
                passwordHash,
                username,
                "",
                1000,
                0,
                0,
                now,
                now
        );
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.updatedAt = Instant.now();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }

    public int getEloRating() {
        return eloRating;
    }

    public void setEloRating(int eloRating) {
        this.eloRating = eloRating;
        this.updatedAt = Instant.now();
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
        this.updatedAt = Instant.now();
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void incrementGamesWon() {
        this.gamesWon++;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = Instant.now();
    }
}
