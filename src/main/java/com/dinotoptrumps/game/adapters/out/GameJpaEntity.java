package com.dinotoptrumps.game.adapters.out;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class GameJpaEntity {

    @Id
    private UUID id;

    @Column(name = "player1_id", nullable = false)
    private UUID player1Id;

    @Column(name = "player2_id")
    private UUID player2Id;

    @Column(nullable = false)
    private String status;

    @Column(name = "current_turn_player_id")
    private UUID currentTurnPlayerId;

    @Column(name = "player1_hand", columnDefinition = "TEXT")
    private String player1Hand;

    @Column(name = "player2_hand", columnDefinition = "TEXT")
    private String player2Hand;

    @Column(name = "winner_id")
    private UUID winnerId;

    @Column(name = "turn_deadline")
    private Instant turnDeadline;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
