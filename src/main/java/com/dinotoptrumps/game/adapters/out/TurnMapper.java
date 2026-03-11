package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Stat;
import com.dinotoptrumps.game.domain.model.Turn;

public class TurnMapper {

    public static TurnJpaEntity toEntity(Turn turn) {
        TurnJpaEntity entity = new TurnJpaEntity();
        entity.setId(turn.getId());
        entity.setGameId(turn.getGameId());
        entity.setTurnNumber(turn.getTurnNumber());
        entity.setActivePlayerId(turn.getActivePlayerId());
        entity.setPlayer1CardId(turn.getPlayer1CardId());
        entity.setPlayer2CardId(turn.getPlayer2CardId());
        entity.setChosenStat(turn.getChosenStat().name());
        entity.setPlayer1StatValue(turn.getPlayer1StatValue());
        entity.setPlayer2StatValue(turn.getPlayer2StatValue());
        entity.setWinnerPlayerId(turn.getWinnerPlayerId());
        entity.setCreatedAt(turn.getCreatedAt());
        return entity;
    }

    public static Turn toDomain(TurnJpaEntity entity) {
        return new Turn(
                entity.getId(),
                entity.getGameId(),
                entity.getTurnNumber(),
                entity.getActivePlayerId(),
                entity.getPlayer1CardId(),
                entity.getPlayer2CardId(),
                Stat.valueOf(entity.getChosenStat()),
                entity.getPlayer1StatValue(),
                entity.getPlayer2StatValue(),
                entity.getWinnerPlayerId(),
                entity.getCreatedAt()
        );
    }
}
