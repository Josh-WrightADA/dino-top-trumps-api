package com.dinotoptrumps.game.ports.out;

import com.dinotoptrumps.game.domain.model.Turn;

import java.util.List;
import java.util.UUID;

public interface ForPersistingTurns {
    Turn save(Turn turn);
    List<Turn> findByGameId(UUID gameId);
    int countByGameId(UUID gameId);
}
