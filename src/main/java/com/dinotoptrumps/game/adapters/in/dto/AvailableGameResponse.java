package com.dinotoptrumps.game.adapters.in.dto;

import java.time.Instant;
import java.util.UUID;

public record AvailableGameResponse(
        UUID id,
        UUID hostId,
        String hostName,
        Instant createdAt
) {
}
