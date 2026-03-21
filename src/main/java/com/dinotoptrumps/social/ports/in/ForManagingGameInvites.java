package com.dinotoptrumps.social.ports.in;

import com.dinotoptrumps.social.domain.model.GameInvite;

import java.util.List;
import java.util.UUID;

public interface ForManagingGameInvites {
    GameInvite sendInvite(UUID inviterId, UUID inviteeId, UUID gameId);
    GameInvite acceptInvite(UUID inviteId, UUID acceptingUserId);
    GameInvite declineInvite(UUID inviteId, UUID decliningUserId);
    List<GameInvite> getPendingInvites(UUID userId);
    int cleanupExpiredInvites();
}
