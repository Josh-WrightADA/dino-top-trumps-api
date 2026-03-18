package com.dinotoptrumps.game.adapters.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameJpaRepository extends JpaRepository<GameJpaEntity, UUID> {

    @Query("SELECT g FROM GameJpaEntity g WHERE (g.player1Id = :playerId OR g.player2Id = :playerId) AND g.status = :status")
    List<GameJpaEntity> findByPlayerIdAndStatus(@Param("playerId") UUID playerId, @Param("status") String status);

    List<GameJpaEntity> findByStatus(String status);

    @Query("SELECT g FROM GameJpaEntity g WHERE (g.player1Id = :playerId OR g.player2Id = :playerId) AND g.status IN (:statuses)")
    List<GameJpaEntity> findByPlayerIdAndStatusIn(@Param("playerId") UUID playerId, @Param("statuses") List<String> statuses);
}
