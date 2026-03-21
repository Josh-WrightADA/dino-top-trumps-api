package com.dinotoptrumps.social.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Friendship {

    private final UUID id;
    private final UUID requesterId;
    private final UUID addresseeId;
    private FriendshipStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public Friendship(UUID id, UUID requesterId, UUID addresseeId,
                      FriendshipStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Friendship create(UUID requesterId, UUID addresseeId) {
        Instant now = Instant.now();
        return new Friendship(
                UUID.randomUUID(),
                requesterId,
                addresseeId,
                FriendshipStatus.PENDING,
                now,
                now
        );
    }

    public void accept() {
        if (status != FriendshipStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot accept a friendship that is not PENDING. Current status: " + status);
        }
        this.status = FriendshipStatus.ACCEPTED;
        this.updatedAt = Instant.now();
    }

    public void decline() {
        if (status != FriendshipStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot decline a friendship that is not PENDING. Current status: " + status);
        }
        this.status = FriendshipStatus.DECLINED;
        this.updatedAt = Instant.now();
    }

    public void remove() {
        if (status != FriendshipStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Cannot remove a friendship that is not ACCEPTED. Current status: " + status);
        }
        this.status = FriendshipStatus.REMOVED;
        this.updatedAt = Instant.now();
    }

    public boolean isParticipant(UUID userId) {
        return requesterId.equals(userId) || addresseeId.equals(userId);
    }

    public boolean isAddressedTo(UUID userId) {
        return addresseeId.equals(userId);
    }

    public boolean isBetween(UUID userA, UUID userB) {
        return (requesterId.equals(userA) && addresseeId.equals(userB))
                || (requesterId.equals(userB) && addresseeId.equals(userA));
    }

    public UUID getId() { return id; }
    public UUID getRequesterId() { return requesterId; }
    public UUID getAddresseeId() { return addresseeId; }
    public FriendshipStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
