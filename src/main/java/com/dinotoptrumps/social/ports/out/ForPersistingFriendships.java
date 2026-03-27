package com.dinotoptrumps.social.ports.out;

import com.dinotoptrumps.social.domain.model.Friendship;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForPersistingFriendships {
    Friendship save(Friendship friendship);
    Optional<Friendship> findById(UUID id);
    Optional<Friendship> findByRequesterAndAddressee(UUID requesterId, UUID addresseeId);
    Optional<Friendship> findAcceptedBetween(UUID userA, UUID userB);
    List<Friendship> findAcceptedByUserId(UUID userId);
    List<Friendship> findPendingForAddressee(UUID addresseeId);
}
