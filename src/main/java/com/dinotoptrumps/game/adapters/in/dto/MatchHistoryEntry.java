package com.dinotoptrumps.game.adapters.in.dto;

import com.dinotoptrumps.game.domain.model.Game;

import java.time.Instant;
import java.util.UUID;

public record MatchHistoryEntry(
        UUID gameId,
        UUID opponentId,
        String opponentName,
        UUID winnerId,
        Instant createdAt
) {
    public static MatchHistoryEntry from(Game game, UUID requestingPlayerId, String opponentName) {
        UUID opponentId = game.isPlayer1(requestingPlayerId)
                ? game.getPlayer2Id()
                : game.getPlayer1Id();

        return new MatchHistoryEntry(
                game.getId(),
                opponentId,
                opponentName,
                game.getWinnerId(),
                game.getCreatedAt()
        );
    }
}
