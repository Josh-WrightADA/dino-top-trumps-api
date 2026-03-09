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
@Table(name = "turns")
@Getter
@Setter
@NoArgsConstructor
public class TurnJpaEntity {

    @Id
    private UUID id;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(name = "turn_number", nullable = false)
    private int turnNumber;

    @Column(name = "active_player_id", nullable = false)
    private UUID activePlayerId;

    @Column(name = "player1_card_id", nullable = false)
    private UUID player1CardId;

    @Column(name = "player2_card_id", nullable = false)
    private UUID player2CardId;

    @Column(name = "chosen_stat", nullable = false)
    private String chosenStat;

    @Column(name = "player1_stat_value", nullable = false)
    private int player1StatValue;

    @Column(name = "player2_stat_value", nullable = false)
    private int player2StatValue;

    @Column(name = "winner_player_id")
    private UUID winnerPlayerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
