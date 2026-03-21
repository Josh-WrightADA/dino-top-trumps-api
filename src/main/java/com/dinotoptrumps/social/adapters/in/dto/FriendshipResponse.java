package com.dinotoptrumps.social.adapters.in.dto;

import com.dinotoptrumps.social.domain.model.Friendship;

import java.time.Instant;
import java.util.UUID;

public record FriendshipResponse(
        UUID id,
        UUID requesterId,
        UUID addresseeId,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static FriendshipResponse from(Friendship friendship) {
        return new FriendshipResponse(
                friendship.getId(),
                friendship.getRequesterId(),
                friendship.getAddresseeId(),
                friendship.getStatus().name(),
                friendship.getCreatedAt(),
                friendship.getUpdatedAt()
        );
    }
}
