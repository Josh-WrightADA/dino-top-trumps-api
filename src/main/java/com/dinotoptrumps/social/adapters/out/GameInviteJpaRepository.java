package com.dinotoptrumps.social.adapters.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameInviteJpaRepository extends JpaRepository<GameInviteJpaEntity, UUID> {

    Optional<GameInviteJpaEntity> findByGameIdAndInviteeIdAndStatus(UUID gameId, UUID inviteeId, String status);

    List<GameInviteJpaEntity> findByInviteeIdAndStatus(UUID inviteeId, String status);

    @Query("SELECT i FROM GameInviteJpaEntity i WHERE i.expiresAt < :now AND i.status = 'PENDING'")
    List<GameInviteJpaEntity> findExpiredPending(@Param("now") Instant now);
}
