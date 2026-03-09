package com.dinotoptrumps.game.ports.in;

import com.dinotoptrumps.game.domain.model.Stat;
import com.dinotoptrumps.game.domain.model.Turn;

import java.util.UUID;

public interface ForPlayingTurn {
    Turn playTurn(UUID gameId, UUID playerId, Stat chosenStat);
}
