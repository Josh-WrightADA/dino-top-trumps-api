package com.dinotoptrumps.auth.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RankTierTest {

    @Test
    void fromElo_below800_returnsHatchling() {
        assertEquals(RankTier.HATCHLING, RankTier.fromElo(0));
        assertEquals(RankTier.HATCHLING, RankTier.fromElo(799));
    }

    @Test
    void fromElo_at800_returnsHerbivore() {
        assertEquals(RankTier.HERBIVORE, RankTier.fromElo(800));
    }

    @Test
    void fromElo_herbivoreRange_returnsHerbivore() {
        assertEquals(RankTier.HERBIVORE, RankTier.fromElo(999));
    }

    @Test
    void fromElo_at1000_returnsCarnivore() {
        assertEquals(RankTier.CARNIVORE, RankTier.fromElo(1000));
    }

    @Test
    void fromElo_carnivoreRange_returnsCarnivore() {
        assertEquals(RankTier.CARNIVORE, RankTier.fromElo(1199));
    }

    @Test
    void fromElo_at1200_returnsApex() {
        assertEquals(RankTier.APEX, RankTier.fromElo(1200));
    }

    @Test
    void fromElo_apexRange_returnsApex() {
        assertEquals(RankTier.APEX, RankTier.fromElo(1399));
    }

    @Test
    void fromElo_at1400_returnsMeteor() {
        assertEquals(RankTier.METEOR, RankTier.fromElo(1400));
    }

    @Test
    void fromElo_above1400_returnsMeteor() {
        assertEquals(RankTier.METEOR, RankTier.fromElo(9999));
    }
}
