package com.dinotoptrumps.social.ports.out;

import java.util.UUID;

public interface ForJoiningGameFromInvite {
    void joinGame(UUID gameId, UUID playerId);
}
