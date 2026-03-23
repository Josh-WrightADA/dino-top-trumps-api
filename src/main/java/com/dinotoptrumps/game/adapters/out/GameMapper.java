package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameEndReason;
import com.dinotoptrumps.game.domain.model.GameStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameMapper {

    public static GameJpaEntity toEntity(Game game) {
        GameJpaEntity entity = new GameJpaEntity();
        entity.setId(game.getId());
        entity.setPlayer1Id(game.getPlayer1Id());
        entity.setPlayer2Id(game.getPlayer2Id());
        entity.setStatus(game.getStatus().name());
        entity.setCurrentTurnPlayerId(game.getCurrentTurnPlayerId());
        entity.setPlayer1Hand(serializeHand(game.getPlayer1Hand()));
        entity.setPlayer2Hand(serializeHand(game.getPlayer2Hand()));
        entity.setDrawPile(serializeHand(game.getDrawPile()));
        entity.setWinnerId(game.getWinnerId());
        entity.setGameEndReason(game.getGameEndReason() != null ? game.getGameEndReason().name() : null);
        entity.setTurnDeadline(game.getTurnDeadline());
        entity.setCreatedAt(game.getCreatedAt());
        entity.setUpdatedAt(game.getUpdatedAt());
        return entity;
    }

    public static Game toDomain(GameJpaEntity entity) {
        return new Game(
                entity.getId(),
                entity.getPlayer1Id(),
                entity.getPlayer2Id(),
                GameStatus.valueOf(entity.getStatus()),
                entity.getCurrentTurnPlayerId(),
                deserializeHand(entity.getPlayer1Hand()),
                deserializeHand(entity.getPlayer2Hand()),
                deserializeHand(entity.getDrawPile()),
                entity.getWinnerId(),
                entity.getGameEndReason() != null ? GameEndReason.valueOf(entity.getGameEndReason()) : null,
                entity.getTurnDeadline(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    static String serializeHand(List<UUID> hand) {
        if (hand == null || hand.isEmpty()) {
            return "";
        }
        return hand.stream()
                .map(UUID::toString)
                .collect(Collectors.joining(","));
    }

    static List<UUID> deserializeHand(String hand) {
        if (hand == null || hand.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(hand.split(","))
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }
}
