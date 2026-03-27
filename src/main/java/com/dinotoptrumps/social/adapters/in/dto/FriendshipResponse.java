package com.dinotoptrumps.social.adapters.in.dto;

import com.dinotoptrumps.social.domain.model.Friendship;

import java.time.Instant;
import java.util.UUID;

public record FriendshipResponse(
        UUID id,
        UUID requesterId,
        String requesterDisplayName,
        UUID addresseeId,
        String addresseeDisplayName,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
    public static FriendshipResponse from(Friendship friendship,
                                          String requesterDisplayName,
                                          String addresseeDisplayName) {
        return new FriendshipResponse(
                friendship.getId(),
                friendship.getRequesterId(),
                requesterDisplayName,
                friendship.getAddresseeId(),
                addresseeDisplayName,
                friendship.getStatus().name(),
                friendship.getCreatedAt(),
                friendship.getUpdatedAt()
        );
    }
}
