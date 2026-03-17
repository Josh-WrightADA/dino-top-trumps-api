package com.dinotoptrumps.game.ports.out;

import java.util.UUID;

public interface ForUpdatingPlayerStats {
    void updateStatsAfterGame(UUID winnerId, UUID loserId);
}
