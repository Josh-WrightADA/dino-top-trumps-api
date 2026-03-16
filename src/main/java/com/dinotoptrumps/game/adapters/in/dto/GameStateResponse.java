package com.dinotoptrumps.game.adapters.in.dto;

import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameStatus;

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
        List<UUID> yourHand,
        UUID winnerId,
        Instant createdAt,
        Instant updatedAt
) {
    /**
     * Builds a response tailored to the requesting player.
     * Only reveals the requesting player's hand — never the opponent's cards.
     */
    public static GameStateResponse forPlayer(Game game, UUID requestingPlayerId) {
        List<UUID> yourHand = game.isPlayer1(requestingPlayerId)
                ? game.getPlayer1Hand()
                : game.getPlayer2Hand();

        return new GameStateResponse(
                game.getId(),
                game.getPlayer1Id(),
                game.getPlayer2Id(),
                game.getStatus(),
                game.getCurrentTurnPlayerId(),
                game.getPlayer1Hand().size(),
                game.getPlayer2Hand().size(),
                yourHand,
                game.getWinnerId(),
                game.getCreatedAt(),
                game.getUpdatedAt()
        );
    }
}
