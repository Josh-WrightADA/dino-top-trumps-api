package com.dinotoptrumps.game.ports.in;

import com.dinotoptrumps.game.domain.model.Game;

import java.util.List;
import java.util.UUID;

public interface ForAdminGameOperations {
    List<Game> getAllGames();
    void deleteGame(UUID gameId);
}
