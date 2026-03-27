package com.dinotoptrumps.social.adapters.out;

import com.dinotoptrumps.social.domain.model.Friendship;
import com.dinotoptrumps.social.domain.model.FriendshipStatus;
import com.dinotoptrumps.social.ports.out.ForPersistingFriendships;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class FriendshipPersistenceAdapter implements ForPersistingFriendships {

    private final FriendshipJpaRepository friendshipJpaRepository;

    public FriendshipPersistenceAdapter(FriendshipJpaRepository friendshipJpaRepository) {
        this.friendshipJpaRepository = friendshipJpaRepository;
    }

    @Override
    public Friendship save(Friendship friendship) {
        FriendshipJpaEntity entity = FriendshipMapper.toEntity(friendship);
        FriendshipJpaEntity saved = friendshipJpaRepository.save(entity);
        return FriendshipMapper.toDomain(saved);
    }

    @Override
    public Optional<Friendship> findById(UUID id) {
        return friendshipJpaRepository.findById(id)
                .map(FriendshipMapper::toDomain);
    }

    @Override
    public Optional<Friendship> findByRequesterAndAddressee(UUID requesterId, UUID addresseeId) {
        return friendshipJpaRepository.findByRequesterIdAndAddresseeId(requesterId, addresseeId)
                .map(FriendshipMapper::toDomain);
    }

    @Override
    public Optional<Friendship> findAcceptedBetween(UUID userA, UUID userB) {
        return friendshipJpaRepository.findAcceptedBetween(userA, userB)
                .map(FriendshipMapper::toDomain);
    }

    @Override
    public List<Friendship> findAcceptedByUserId(UUID userId) {
        return friendshipJpaRepository.findAcceptedByUserId(userId).stream()
                .map(FriendshipMapper::toDomain)
                .toList();
    }

    @Override
    public List<Friendship> findPendingForAddressee(UUID addresseeId) {
        return friendshipJpaRepository.findByAddresseeIdAndStatus(
                addresseeId, FriendshipStatus.PENDING.name()).stream()
                .map(FriendshipMapper::toDomain)
                .toList();
    }
}
