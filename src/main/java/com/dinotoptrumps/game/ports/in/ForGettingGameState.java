package com.dinotoptrumps.game.ports.in;

import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.Turn;

import java.util.Optional;
import java.util.UUID;

public interface ForGettingGameState {
    Game getGameState(UUID gameId);
    Optional<Turn> getLastTurn(UUID gameId);
}
