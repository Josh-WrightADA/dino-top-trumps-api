package com.dinotoptrumps.game.ports.in;

import com.dinotoptrumps.game.domain.model.Game;

import java.util.List;

public interface ForListingGames {
    List<Game> getAvailableGames();
    List<Game> getActiveGames(java.util.UUID playerId);
}
