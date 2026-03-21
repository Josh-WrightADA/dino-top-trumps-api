package com.dinotoptrumps.social.ports.out;

import com.dinotoptrumps.social.domain.model.GameInvite;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForPersistingGameInvites {
    GameInvite save(GameInvite invite);
    Optional<GameInvite> findById(UUID id);
    Optional<GameInvite> findPendingByGameAndInvitee(UUID gameId, UUID inviteeId);
    List<GameInvite> findPendingByInviteeId(UUID inviteeId);
    List<GameInvite> findExpiredPending(Instant now);
}
