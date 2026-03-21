package com.dinotoptrumps.game.domain.service;

import com.dinotoptrumps.game.domain.model.Card;
import com.dinotoptrumps.game.domain.model.Stat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StatComparisonServiceTest {

    private StatComparisonService service;
    private Card tRex;
    private Card velociraptor;

    @BeforeEach
    void setUp() {
        service = new StatComparisonService();

        tRex = new Card(
                UUID.randomUUID(), "T-Rex", "Tyrant lizard", "carnivorous",
                "Late Cretaceous", "http://example.com/trex.jpg", "", null,
                85, 90, 40, 30, 95
        );

        velociraptor = new Card(
                UUID.randomUUID(), "Velociraptor", "Swift thief", "carnivorous",
                "Late Cretaceous", "http://example.com/raptor.jpg", "", null,
                25, 15, 90, 95, 40
        );
    }

    @Test
    void higherStatWins() {
        UUID winner = service.compare(tRex, velociraptor, Stat.STRENGTH);
        assertEquals(tRex.getId(), winner);
    }

    @Test
    void lowerStatLoses() {
        UUID winner = service.compare(tRex, velociraptor, Stat.SPEED);
        assertEquals(velociraptor.getId(), winner);
    }

    @Test
    void equalStats_shouldReturnNull() {
        Card card1 = new Card(
                UUID.randomUUID(), "Dino A", "", "", "", "", "", null,
                50, 50, 70, 50, 50
        );
        Card card2 = new Card(
                UUID.randomUUID(), "Dino B", "", "", "", "", "", null,
                50, 50, 70, 50, 50
        );
        UUID winner = service.compare(card1, card2, Stat.INTELLIGENCE);
        assertNull(winner);
    }

    @Test
    void allStatsCanBeCompared() {
        Card card1 = new Card(
                UUID.randomUUID(), "Dino A", "", "", "", "", "", null,
                50, 50, 70, 50, 50
        );
        Card card2 = new Card(
                UUID.randomUUID(), "Dino B", "", "", "", "", "", null,
                50, 50, 70, 50, 50
        );

        for (Stat stat : Stat.values()) {
            assertDoesNotThrow(() -> service.compare(card1, card2, stat));
        }
    }
}
