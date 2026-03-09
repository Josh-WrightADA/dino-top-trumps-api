package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Game;

public class GameMapper {

    public static GameJpaEntity toEntity(Game game) {
        // TODO: Map domain Game to JPA entity, serialize hand lists to JSON
        return new GameJpaEntity();
    }

    public static Game toDomain(GameJpaEntity entity) {
        // TODO: Map JPA entity to domain Game, deserialize hand lists from JSON
        return null;
    }
}
