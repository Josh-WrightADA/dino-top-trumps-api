package com.dinotoptrumps.game.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Turn {

    private final UUID id;
    private final UUID gameId;
    private final int turnNumber;
    private final UUID activePlayerId;
    private final UUID player1CardId;
    private final UUID player2CardId;
    private final Stat chosenStat;
    private final int player1StatValue;
    private final int player2StatValue;
    private final UUID winnerPlayerId;
    private final Instant createdAt;

    public Turn(UUID id, UUID gameId, int turnNumber, UUID activePlayerId,
                UUID player1CardId, UUID player2CardId, Stat chosenStat,
                int player1StatValue, int player2StatValue, UUID winnerPlayerId,
                Instant createdAt) {
        this.id = id;
        this.gameId = gameId;
        this.turnNumber = turnNumber;
        this.activePlayerId = activePlayerId;
        this.player1CardId = player1CardId;
        this.player2CardId = player2CardId;
        this.chosenStat = chosenStat;
        this.player1StatValue = player1StatValue;
        this.player2StatValue = player2StatValue;
        this.winnerPlayerId = winnerPlayerId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getGameId() {
        return gameId;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public UUID getActivePlayerId() {
        return activePlayerId;
    }

    public UUID getPlayer1CardId() {
        return player1CardId;
    }

    public UUID getPlayer2CardId() {
        return player2CardId;
    }

    public Stat getChosenStat() {
        return chosenStat;
    }

    public int getPlayer1StatValue() {
        return player1StatValue;
    }

    public int getPlayer2StatValue() {
        return player2StatValue;
    }

    public UUID getWinnerPlayerId() {
        return winnerPlayerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
