package com.dinotoptrumps.game.ports.in;

import com.dinotoptrumps.game.domain.model.Game;

import java.util.UUID;

public interface ForForfeitingGame {
    Game forfeitGame(UUID gameId, UUID playerId);
}
