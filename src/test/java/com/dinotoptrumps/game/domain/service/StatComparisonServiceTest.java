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

        // T-Rex: strong and heavy, but slow and not very smart
        tRex = new Card(
                UUID.randomUUID(), "T-Rex", "Tyrant lizard", "carnivorous",
                "Late Cretaceous", "http://example.com/trex.jpg",
                85, 90, 40, 30, 95
        );

        // Velociraptor: fast and intelligent, but small and light
        velociraptor = new Card(
                UUID.randomUUID(), "Velociraptor", "Swift thief", "carnivorous",
                "Late Cretaceous", "http://example.com/raptor.jpg",
                25, 15, 90, 95, 40
        );
    }

    @Test
    void higherStatWins() {
        // T-Rex has 95 strength vs Velociraptor's 40
        UUID winner = service.compare(tRex, velociraptor, Stat.STRENGTH);
        // TODO: Assert T-Rex wins on strength
    }

    @Test
    void lowerStatLoses() {
        // T-Rex has 30 speed vs Velociraptor's 95
        UUID winner = service.compare(tRex, velociraptor, Stat.SPEED);
        // TODO: Assert Velociraptor wins on speed
    }

    @Test
    void equalStats_shouldReturnNull() {
        // Create two cards with identical intelligence
        Card card1 = new Card(
                UUID.randomUUID(), "Dino A", "", "", "", "",
                50, 50, 70, 50, 50
        );
        Card card2 = new Card(
                UUID.randomUUID(), "Dino B", "", "", "", "",
                50, 50, 70, 50, 50
        );
        UUID winner = service.compare(card1, card2, Stat.INTELLIGENCE);
        // TODO: Assert winner is null (draw)
    }

    @Test
    void allStatsCanBeCompared() {
        // TODO: Test that every Stat enum value works with compare()
        // Loop through Stat.values() and call compare for each
        // Assert no exceptions are thrown
    }
}
