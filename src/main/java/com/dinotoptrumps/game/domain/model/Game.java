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

    // --- Getters ---

    public UUID getId() { return id; }
    public UUID getPlayer1Id() { return player1Id; }
    public UUID getPlayer2Id() { return player2Id; }
    public GameStatus getStatus() { return status; }
    public UUID getCurrentTurnPlayerId() { return currentTurnPlayerId; }
    public List<UUID> getPlayer1Hand() { return player1Hand; }
    public List<UUID> getPlayer2Hand() { return player2Hand; }
    public UUID getWinnerId() { return winnerId; }
    public Instant getTurnDeadline() { return turnDeadline; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // --- Domain behaviour ---

    public boolean isPlayer1(UUID playerId) {
        return player1Id.equals(playerId);
    }

    /**
     * Sets up the game when a second player joins: deals cards and starts play.
     */
    public void start(UUID joiningPlayerId, Hand[] dealtHands) {
        this.player2Id = joiningPlayerId;
        this.player1Hand = dealtHands[0].getCardIds();
        this.player2Hand = dealtHands[1].getCardIds();
        this.status = GameStatus.IN_PROGRESS;
        this.currentTurnPlayerId = player1Id;
        this.updatedAt = Instant.now();
    }

    /**
     * Resolves a round of Top Trumps: removes top cards, awards them to the winner,
     * and sets the next turn according to authentic rules (winner keeps turn).
     *
     * @param winningCardId the ID of the winning card, or null for a draw
     * @param p1CardId player 1's card in this round
     * @param p2CardId player 2's card in this round
     * @return the winning player's UUID, or null for a draw
     */
    public UUID resolveRound(UUID winningCardId, UUID p1CardId, UUID p2CardId) {
        Hand p1Hand = new Hand(player1Hand);
        Hand p2Hand = new Hand(player2Hand);

        p1Hand.removeTopCard();
        p2Hand.removeTopCard();

        UUID turnWinnerPlayerId = null;

        if (winningCardId == null) {
            p1Hand.addCardsToBottom(List.of(p1CardId));
            p2Hand.addCardsToBottom(List.of(p2CardId));
        } else if (winningCardId.equals(p1CardId)) {
            p1Hand.addCardsToBottom(List.of(p1CardId, p2CardId));
            currentTurnPlayerId = player1Id;
            turnWinnerPlayerId = player1Id;
        } else {
            p2Hand.addCardsToBottom(List.of(p1CardId, p2CardId));
            currentTurnPlayerId = player2Id;
            turnWinnerPlayerId = player2Id;
        }

        this.player1Hand = p1Hand.getCardIds();
        this.player2Hand = p2Hand.getCardIds();
        this.updatedAt = Instant.now();

        return turnWinnerPlayerId;
    }

    /**
     * Checks if the game is over (either player has no cards left)
     * and transitions to FINISHED if so.
     */
    public void checkGameOver() {
        if (player1Hand.isEmpty()) {
            this.winnerId = player2Id;
            this.status = GameStatus.FINISHED;
            this.updatedAt = Instant.now();
        } else if (player2Hand.isEmpty()) {
            this.winnerId = player1Id;
            this.status = GameStatus.FINISHED;
            this.updatedAt = Instant.now();
        }
    }
}
