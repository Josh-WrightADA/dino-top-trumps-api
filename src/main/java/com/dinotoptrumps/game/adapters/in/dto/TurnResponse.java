package com.dinotoptrumps.game.adapters.in.dto;

import com.dinotoptrumps.game.domain.model.Stat;
import com.dinotoptrumps.game.domain.model.Turn;

import java.time.Instant;
import java.util.UUID;

public record TurnResponse(
        UUID id,
        UUID gameId,
        int turnNumber,
        UUID activePlayerId,
        UUID player1CardId,
        UUID player2CardId,
        Stat chosenStat,
        int player1StatValue,
        int player2StatValue,
        UUID winnerPlayerId,
        Instant createdAt
) {
    public static TurnResponse from(Turn turn) {
        return new TurnResponse(
                turn.getId(),
                turn.getGameId(),
                turn.getTurnNumber(),
                turn.getActivePlayerId(),
                turn.getPlayer1CardId(),
                turn.getPlayer2CardId(),
                turn.getChosenStat(),
                turn.getPlayer1StatValue(),
                turn.getPlayer2StatValue(),
                turn.getWinnerPlayerId(),
                turn.getCreatedAt()
        );
    }
}
