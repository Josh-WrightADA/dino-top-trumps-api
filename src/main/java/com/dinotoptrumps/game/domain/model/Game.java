package com.dinotoptrumps.game.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Game {

    private final UUID id;
    private final UUID player1Id;
    private UUID player2Id;
    private GameStatus status;
    private UUID currentTurnPlayerId;
    private List<UUID> player1Hand;
    private List<UUID> player2Hand;
    private UUID winnerId;
    private Instant turnDeadline;
    private final Instant createdAt;
    private Instant updatedAt;

    public Game(UUID id, UUID player1Id, UUID player2Id, GameStatus status,
                UUID currentTurnPlayerId, List<UUID> player1Hand, List<UUID> player2Hand,
                UUID winnerId, Instant turnDeadline, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.status = status;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.player1Hand = player1Hand;
        this.player2Hand = player2Hand;
        this.winnerId = winnerId;
        this.turnDeadline = turnDeadline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Game create(UUID player1Id) {
        Instant now = Instant.now();
        return new Game(
                UUID.randomUUID(),
                player1Id,
                null,
                GameStatus.WAITING,
                null,
                List.of(),
                List.of(),
                null,
                null,
                now,
                now
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getPlayer1Id() {
        return player1Id;
    }

    public UUID getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(UUID player2Id) {
        this.player2Id = player2Id;
        this.updatedAt = Instant.now();
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public UUID getCurrentTurnPlayerId() {
        return currentTurnPlayerId;
    }

    public void setCurrentTurnPlayerId(UUID currentTurnPlayerId) {
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.updatedAt = Instant.now();
    }

    public List<UUID> getPlayer1Hand() {
        return player1Hand;
    }

    public void setPlayer1Hand(List<UUID> player1Hand) {
        this.player1Hand = player1Hand;
        this.updatedAt = Instant.now();
    }

    public List<UUID> getPlayer2Hand() {
        return player2Hand;
    }

    public void setPlayer2Hand(List<UUID> player2Hand) {
        this.player2Hand = player2Hand;
        this.updatedAt = Instant.now();
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
        this.updatedAt = Instant.now();
    }

    public Instant getTurnDeadline() {
        return turnDeadline;
    }

    public void setTurnDeadline(Instant turnDeadline) {
        this.turnDeadline = turnDeadline;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
