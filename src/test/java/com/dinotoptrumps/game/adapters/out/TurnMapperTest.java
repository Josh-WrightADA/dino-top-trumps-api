package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Stat;
import com.dinotoptrumps.game.domain.model.Turn;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TurnMapperTest {

    @Test
    void roundTrip_preservesAllFields() {
        UUID turnId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID activePlayer = UUID.randomUUID();
        UUID p1Card = UUID.randomUUID();
        UUID p2Card = UUID.randomUUID();
        UUID winner = activePlayer;
        Instant now = Instant.now();

        Turn original = new Turn(
                turnId, gameId, 3, activePlayer,
                p1Card, p2Card, Stat.SPEED,
                85, 72, winner, now
        );

        TurnJpaEntity entity = TurnMapper.toEntity(original);
        Turn restored = TurnMapper.toDomain(entity);

        assertEquals(turnId, restored.getId());
        assertEquals(gameId, restored.getGameId());
        assertEquals(3, restored.getTurnNumber());
        assertEquals(activePlayer, restored.getActivePlayerId());
        assertEquals(p1Card, restored.getPlayer1CardId());
        assertEquals(p2Card, restored.getPlayer2CardId());
        assertEquals(Stat.SPEED, restored.getChosenStat());
        assertEquals(85, restored.getPlayer1StatValue());
        assertEquals(72, restored.getPlayer2StatValue());
        assertEquals(winner, restored.getWinnerPlayerId());
        assertEquals(now, restored.getCreatedAt());
    }

    @Test
    void roundTrip_drawTurn_nullWinner() {
        Turn original = new Turn(
                UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), Stat.INTELLIGENCE,
                50, 50, null, Instant.now()
        );

        TurnJpaEntity entity = TurnMapper.toEntity(original);
        Turn restored = TurnMapper.toDomain(entity);

        assertNull(restored.getWinnerPlayerId());
        assertEquals(Stat.INTELLIGENCE, restored.getChosenStat());
    }

    @Test
    void toEntity_setsStatAsString() {
        Turn turn = new Turn(
                UUID.randomUUID(), UUID.randomUUID(), 1, UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), Stat.STRENGTH,
                90, 45, UUID.randomUUID(), Instant.now()
        );

        TurnJpaEntity entity = TurnMapper.toEntity(turn);

        assertEquals("STRENGTH", entity.getChosenStat());
    }
}
