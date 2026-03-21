package com.dinotoptrumps.social.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class GameInvite {

    private static final int INVITE_EXPIRY_MINUTES = 5;

    private final UUID id;
    private final UUID gameId;
    private final UUID inviterId;
    private final UUID inviteeId;
    private GameInviteStatus status;
    private final Instant expiresAt;
    private final Instant createdAt;

    public GameInvite(UUID id, UUID gameId, UUID inviterId, UUID inviteeId,
                      GameInviteStatus status, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.gameId = gameId;
        this.inviterId = inviterId;
        this.inviteeId = inviteeId;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static GameInvite create(UUID gameId, UUID inviterId, UUID inviteeId) {
        Instant now = Instant.now();
        return new GameInvite(
                UUID.randomUUID(),
                gameId,
                inviterId,
                inviteeId,
                GameInviteStatus.PENDING,
                now.plus(INVITE_EXPIRY_MINUTES, ChronoUnit.MINUTES),
                now
        );
    }

    public void accept() {
        if (status != GameInviteStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot accept an invite that is not PENDING. Current status: " + status);
        }
        if (isExpired()) {
            throw new IllegalStateException("Cannot accept an expired invite");
        }
        this.status = GameInviteStatus.ACCEPTED;
    }

    public void decline() {
        if (status != GameInviteStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot decline an invite that is not PENDING. Current status: " + status);
        }
        this.status = GameInviteStatus.DECLINED;
    }

    public void expire() {
        this.status = GameInviteStatus.EXPIRED;
    }

    public void cancel() {
        if (status != GameInviteStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot cancel an invite that is not PENDING. Current status: " + status);
        }
        this.status = GameInviteStatus.CANCELLED;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isAddressedTo(UUID userId) {
        return inviteeId.equals(userId);
    }

    public UUID getId() { return id; }
    public UUID getGameId() { return gameId; }
    public UUID getInviterId() { return inviterId; }
    public UUID getInviteeId() { return inviteeId; }
    public GameInviteStatus getStatus() { return status; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
}
