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
    private String bio;
    private UUID favouriteCardId;
    private Role role;
    private AccountStatus status;
    private int eloRating;
    private int gamesPlayed;
    private int gamesWon;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UUID id, String username, String email, String passwordHash,
                String displayName, String avatarUrl, String bio, UUID favouriteCardId,
                Role role, AccountStatus status,
                int eloRating, int gamesPlayed, int gamesWon,
                Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.favouriteCardId = favouriteCardId;
        this.role = role;
        this.status = status;
        this.eloRating = eloRating;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private static final int DEFAULT_ELO_RATING = 1000;

    public static User create(String username, String email, String passwordHash) {
        Instant now = Instant.now();
        return new User(
                UUID.randomUUID(),
                username,
                email,
                passwordHash,
                username,
                "",
                null,
                null,
                Role.PLAYER,
                AccountStatus.ACTIVE,
                DEFAULT_ELO_RATING,
                0,
                0,
                now,
                now
        );
    }

    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }

    public boolean isBanned() {
        return AccountStatus.BANNED.equals(this.status);
    }

    public void ban() {
        this.status = AccountStatus.BANNED;
        this.updatedAt = Instant.now();
    }

    public void unban() {
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
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

    public void changeDisplayName(String displayName) {
        this.displayName = displayName;
        this.updatedAt = Instant.now();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void changeAvatar(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = Instant.now();
    }

    public String getBio() {
        return bio;
    }

    public void updateBio(String bio) {
        this.bio = bio;
        this.updatedAt = Instant.now();
    }

    public UUID getFavouriteCardId() {
        return favouriteCardId;
    }

    public void chooseFavouriteCard(UUID favouriteCardId) {
        this.favouriteCardId = favouriteCardId;
        this.updatedAt = Instant.now();
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public int getEloRating() {
        return eloRating;
    }

    public void adjustEloRating(int eloRating) {
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

    public void resetPasswordTo(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = Instant.now();
    }
}
