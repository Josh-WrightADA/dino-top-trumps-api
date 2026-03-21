package com.dinotoptrumps.social.adapters.in.dto;

import com.dinotoptrumps.social.domain.model.GameInvite;

import java.time.Instant;
import java.util.UUID;

public record GameInviteResponse(
        UUID id,
        UUID gameId,
        UUID inviterId,
        UUID inviteeId,
        String status,
        Instant expiresAt,
        Instant createdAt
) {
    public static GameInviteResponse from(GameInvite invite) {
        return new GameInviteResponse(
                invite.getId(),
                invite.getGameId(),
                invite.getInviterId(),
                invite.getInviteeId(),
                invite.getStatus().name(),
                invite.getExpiresAt(),
                invite.getCreatedAt()
        );
    }
}
