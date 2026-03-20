package com.dinotoptrumps.game.ports.out;

import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForPersistingGames {
    Game save(Game game);
    Optional<Game> findById(UUID id);
    List<Game> findByPlayerIdAndStatus(UUID playerId, GameStatus status);
    List<Game> findByStatus(GameStatus status);
    List<Game> findActiveByPlayerId(UUID playerId);
    List<Game> findStaleGames(Instant waitingBefore, Instant timedOutBefore);
}
