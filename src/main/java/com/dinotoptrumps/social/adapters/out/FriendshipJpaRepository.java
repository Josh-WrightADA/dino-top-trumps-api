package com.dinotoptrumps.social.adapters.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipJpaRepository extends JpaRepository<FriendshipJpaEntity, UUID> {

    Optional<FriendshipJpaEntity> findByRequesterIdAndAddresseeId(UUID requesterId, UUID addresseeId);

    @Query("SELECT f FROM FriendshipJpaEntity f WHERE "
            + "((f.requesterId = :userA AND f.addresseeId = :userB) OR "
            + "(f.requesterId = :userB AND f.addresseeId = :userA)) AND f.status = 'ACCEPTED'")
    Optional<FriendshipJpaEntity> findAcceptedBetween(@Param("userA") UUID userA, @Param("userB") UUID userB);

    @Query("SELECT f FROM FriendshipJpaEntity f WHERE "
            + "(f.requesterId = :userId OR f.addresseeId = :userId) AND f.status = 'ACCEPTED'")
    List<FriendshipJpaEntity> findAcceptedByUserId(@Param("userId") UUID userId);

    List<FriendshipJpaEntity> findByAddresseeIdAndStatus(UUID addresseeId, String status);
}
