package com.dinotoptrumps.game.ports.in;

import com.dinotoptrumps.game.domain.model.Game;

import java.util.List;
import java.util.UUID;

public interface ForGettingMatchHistory {
    List<Game> getMatchHistory(UUID playerId);
}
