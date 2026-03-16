package com.dinotoptrumps.game.adapters.in.dto;

import com.dinotoptrumps.game.domain.model.Game;

import java.time.Instant;
import java.util.UUID;

public record MatchHistoryEntry(
        UUID gameId,
        UUID opponentId,
        UUID winnerId,
        Instant createdAt
) {
    public static MatchHistoryEntry from(Game game, UUID requestingPlayerId) {
        UUID opponentId = game.isPlayer1(requestingPlayerId)
                ? game.getPlayer2Id()
                : game.getPlayer1Id();

        return new MatchHistoryEntry(
                game.getId(),
                opponentId,
                game.getWinnerId(),
                game.getCreatedAt()
        );
    }
}
