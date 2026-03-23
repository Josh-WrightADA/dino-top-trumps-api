package com.dinotoptrumps.social.ports.out;

import java.util.UUID;

public interface ForCheckingGameStatus {
    boolean isGameWaitingAndHostedBy(UUID gameId, UUID hostId);
}
