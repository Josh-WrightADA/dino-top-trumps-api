package com.dinotoptrumps.game.adapters.in.dto;

import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameStatus;
import com.dinotoptrumps.game.domain.model.Turn;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GameStateResponse(
        UUID id,
        UUID player1Id,
        UUID player2Id,
        GameStatus status,
        UUID currentTurnPlayerId,
        int player1HandSize,
        int player2HandSize,
        int drawPileSize,
        List<UUID> yourHand,
        UUID winnerId,
        String gameEndReason,
        Instant turnDeadline,
        Instant createdAt,
        Instant updatedAt,
        TurnResponse lastTurn
) {
    /**
     * Builds a response tailored to the requesting player.
     * Only reveals the requesting player's hand — never the opponent's cards.
     */
    public static GameStateResponse forPlayer(Game game, UUID requestingPlayerId) {
        return forPlayer(game, requestingPlayerId, null);
    }

    public static GameStateResponse forPlayer(Game game, UUID requestingPlayerId, Turn lastTurn) {
        List<UUID> yourHand = game.isPlayer1(requestingPlayerId)
                ? game.getPlayer1Hand()
                : game.getPlayer2Hand();

        TurnResponse turnResponse = lastTurn != null ? TurnResponse.from(lastTurn) : null;

        return new GameStateResponse(
                game.getId(),
                game.getPlayer1Id(),
                game.getPlayer2Id(),
                game.getStatus(),
                game.getCurrentTurnPlayerId(),
                game.getPlayer1Hand().size(),
                game.getPlayer2Hand().size(),
                game.getDrawPile().size(),
                yourHand,
                game.getWinnerId(),
                game.getGameEndReason() != null ? game.getGameEndReason().name() : null,
                game.getTurnDeadline(),
                game.getCreatedAt(),
                game.getUpdatedAt(),
                turnResponse
        );
    }
}
