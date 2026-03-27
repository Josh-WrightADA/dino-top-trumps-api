package com.dinotoptrumps.social.adapters.in.dto;

import com.dinotoptrumps.social.domain.model.GameInvite;

import java.time.Instant;
import java.util.UUID;

public record GameInviteResponse(
        UUID id,
        UUID gameId,
        UUID inviterId,
        String inviterDisplayName,
        UUID inviteeId,
        String inviteeDisplayName,
        String status,
        Instant expiresAt,
        Instant createdAt
) {
    public static GameInviteResponse from(GameInvite invite,
                                          String inviterDisplayName,
                                          String inviteeDisplayName) {
        return new GameInviteResponse(
                invite.getId(),
                invite.getGameId(),
                invite.getInviterId(),
                inviterDisplayName,
                invite.getInviteeId(),
                inviteeDisplayName,
                invite.getStatus().name(),
                invite.getExpiresAt(),
                invite.getCreatedAt()
        );
    }
}
