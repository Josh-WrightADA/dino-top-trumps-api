package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    // --- Hand serialization tests ---

    @Test
    void serializeHand_withMultipleUuids_returnsCommaSeparated() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        String result = GameMapper.serializeHand(List.of(id1, id2, id3));

        assertEquals(id1 + "," + id2 + "," + id3, result);
    }

    @Test
    void serializeHand_withSingleUuid_returnsJustThatUuid() {
        UUID id = UUID.randomUUID();

        String result = GameMapper.serializeHand(List.of(id));

        assertEquals(id.toString(), result);
    }

    @Test
    void serializeHand_withEmptyList_returnsEmptyString() {
        assertEquals("", GameMapper.serializeHand(List.of()));
    }

    @Test
    void serializeHand_withNull_returnsEmptyString() {
        assertEquals("", GameMapper.serializeHand(null));
    }

    @Test
    void deserializeHand_withCommaSeparated_returnsList() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        String input = id1 + "," + id2;

        List<UUID> result = GameMapper.deserializeHand(input);

        assertEquals(List.of(id1, id2), result);
    }

    @Test
    void deserializeHand_withSingleUuid_returnsSingletonList() {
        UUID id = UUID.randomUUID();

        List<UUID> result = GameMapper.deserializeHand(id.toString());

        assertEquals(List.of(id), result);
    }

    @Test
    void deserializeHand_withEmptyString_returnsEmptyList() {
        assertTrue(GameMapper.deserializeHand("").isEmpty());
    }

    @Test
    void deserializeHand_withNull_returnsEmptyList() {
        assertTrue(GameMapper.deserializeHand(null).isEmpty());
    }

    @Test
    void deserializeHand_withBlankString_returnsEmptyList() {
        assertTrue(GameMapper.deserializeHand("   ").isEmpty());
    }

    // --- Round-trip tests ---

    @Test
    void roundTrip_gameWithHands_preservesAllFields() {
        UUID gameId = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID currentTurn = p1;
        UUID card1 = UUID.randomUUID();
        UUID card2 = UUID.randomUUID();
        UUID card3 = UUID.randomUUID();
        Instant now = Instant.now();

        Game original = new Game(
                gameId, p1, p2, GameStatus.IN_PROGRESS, currentTurn,
                List.of(card1, card2), List.of(card3),
                List.of(), null, null, now, now
        );

        GameJpaEntity entity = GameMapper.toEntity(original);
        Game restored = GameMapper.toDomain(entity);

        assertEquals(gameId, restored.getId());
        assertEquals(p1, restored.getPlayer1Id());
        assertEquals(p2, restored.getPlayer2Id());
        assertEquals(GameStatus.IN_PROGRESS, restored.getStatus());
        assertEquals(currentTurn, restored.getCurrentTurnPlayerId());
        assertEquals(List.of(card1, card2), restored.getPlayer1Hand());
        assertEquals(List.of(card3), restored.getPlayer2Hand());
        assertNull(restored.getWinnerId());
        assertEquals(now, restored.getCreatedAt());
    }

    @Test
    void roundTrip_waitingGameWithEmptyHands_preservesState() {
        UUID gameId = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        Instant now = Instant.now();

        Game original = new Game(
                gameId, p1, null, GameStatus.WAITING, null,
                List.of(), List.of(),
                List.of(), null, null, now, now
        );

        GameJpaEntity entity = GameMapper.toEntity(original);
        Game restored = GameMapper.toDomain(entity);

        assertEquals(GameStatus.WAITING, restored.getStatus());
        assertNull(restored.getPlayer2Id());
        assertTrue(restored.getPlayer1Hand().isEmpty());
        assertTrue(restored.getPlayer2Hand().isEmpty());
    }
}
