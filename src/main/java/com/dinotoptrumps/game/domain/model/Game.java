package com.dinotoptrumps.game.domain.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
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
    private List<UUID> drawPile;
    private UUID winnerId;
    private GameEndReason gameEndReason;
    private Instant turnDeadline;
    private final Instant createdAt;
    private Instant updatedAt;

    private static final int TURN_TIME_SECONDS = 30;
    private static final int CEREMONY_BUFFER_SECONDS = 12;

    public Game(UUID id, UUID player1Id, UUID player2Id, GameStatus status,
                UUID currentTurnPlayerId, List<UUID> player1Hand, List<UUID> player2Hand,
                List<UUID> drawPile, UUID winnerId, GameEndReason gameEndReason,
                Instant turnDeadline, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.status = status;
        this.currentTurnPlayerId = currentTurnPlayerId;
        this.player1Hand = player1Hand;
        this.player2Hand = player2Hand;
        this.drawPile = drawPile;
        this.winnerId = winnerId;
        this.gameEndReason = gameEndReason;
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
                List.of(),
                null,
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
    public List<UUID> getPlayer1Hand() { return Collections.unmodifiableList(player1Hand); }
    public List<UUID> getPlayer2Hand() { return Collections.unmodifiableList(player2Hand); }
    public List<UUID> getDrawPile() { return Collections.unmodifiableList(drawPile); }
    public UUID getWinnerId() { return winnerId; }
    public GameEndReason getGameEndReason() { return gameEndReason; }
    public Instant getTurnDeadline() { return turnDeadline; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // --- Domain behaviour ---

    public boolean isPlayer1(UUID playerId) {
        return player1Id.equals(playerId);
    }

    public boolean isTimedOut() {
        return turnDeadline != null && Instant.now().isAfter(turnDeadline);
    }

    /**
     * Sets up the game when a second player joins: deals cards and starts play.
     */
    public void start(UUID joiningPlayerId, Hand[] dealtHands) {
        Instant now = Instant.now();
        this.player2Id = joiningPlayerId;
        this.player1Hand = dealtHands[0].getCardIds();
        this.player2Hand = dealtHands[1].getCardIds();
        this.drawPile = new ArrayList<>();
        this.status = GameStatus.IN_PROGRESS;
        this.currentTurnPlayerId = Math.random() < 0.5 ? player1Id : joiningPlayerId;
        this.turnDeadline = now.plus(TURN_TIME_SECONDS + CEREMONY_BUFFER_SECONDS, ChronoUnit.SECONDS);
        this.updatedAt = now;
    }

    /**
     * Resolves a round of Top Trumps using authentic draw pile rules.
     * On draw: both cards go to the draw pile.
     * On win: winner gets both cards plus all cards from the draw pile.
     * Winner keeps the turn (authentic rules).
     */
    public UUID resolveRound(UUID winningCardId, UUID p1CardId, UUID p2CardId) {
        Hand p1Hand = new Hand(player1Hand);
        Hand p2Hand = new Hand(player2Hand);

        p1Hand.removeTopCard();
        p2Hand.removeTopCard();

        UUID turnWinnerPlayerId = null;

        if (winningCardId == null) {
            List<UUID> pile = new ArrayList<>(drawPile);
            pile.add(p1CardId);
            pile.add(p2CardId);
            this.drawPile = pile;
        } else {
            List<UUID> wonCards = new ArrayList<>();
            wonCards.add(p1CardId);
            wonCards.add(p2CardId);
            wonCards.addAll(drawPile);
            this.drawPile = new ArrayList<>();

            if (winningCardId.equals(p1CardId)) {
                p1Hand.addCardsToBottom(wonCards);
                currentTurnPlayerId = player1Id;
                turnWinnerPlayerId = player1Id;
            } else {
                p2Hand.addCardsToBottom(wonCards);
                currentTurnPlayerId = player2Id;
                turnWinnerPlayerId = player2Id;
            }
        }

        this.player1Hand = p1Hand.getCardIds();
        this.player2Hand = p2Hand.getCardIds();
        Instant now = Instant.now();
        this.turnDeadline = now.plus(TURN_TIME_SECONDS, ChronoUnit.SECONDS);
        this.updatedAt = now;

        return turnWinnerPlayerId;
    }

    /**
     * Checks if the game is over (either player has no cards left)
     * and transitions to FINISHED if so.
     */
    public void checkGameOver() {
        if (player1Hand.isEmpty()) {
            this.winnerId = player2Id;
            this.gameEndReason = GameEndReason.NORMAL;
            this.status = GameStatus.FINISHED;
            this.turnDeadline = null;
            this.updatedAt = Instant.now();
        } else if (player2Hand.isEmpty()) {
            this.winnerId = player1Id;
            this.gameEndReason = GameEndReason.NORMAL;
            this.status = GameStatus.FINISHED;
            this.turnDeadline = null;
            this.updatedAt = Instant.now();
        }
    }

    /**
     * Forfeits the game — the specified winner wins by forfeit.
     */
    public void forfeit(UUID winningPlayerId, GameEndReason reason) {
        this.winnerId = winningPlayerId;
        this.gameEndReason = reason;
        this.status = GameStatus.FINISHED;
        this.turnDeadline = null;
        this.updatedAt = Instant.now();
    }
}
