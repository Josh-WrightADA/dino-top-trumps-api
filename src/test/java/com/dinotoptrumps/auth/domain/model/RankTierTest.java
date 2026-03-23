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

    @Test
    void getFloorElo_eachTier() {
        assertEquals(0, RankTier.HATCHLING.getFloorElo());
        assertEquals(800, RankTier.HERBIVORE.getFloorElo());
        assertEquals(1000, RankTier.CARNIVORE.getFloorElo());
        assertEquals(1200, RankTier.APEX.getFloorElo());
        assertEquals(1400, RankTier.METEOR.getFloorElo());
    }

    @Test
    void calculateLeaguePoints_midTier() {
        // ELO 1050 is 50 above Carnivore floor (1000)
        assertEquals(50, RankTier.calculateLeaguePoints(1050));
    }

    @Test
    void calculateLeaguePoints_tierFloor() {
        // ELO exactly at tier floor → 0 LP
        assertEquals(0, RankTier.calculateLeaguePoints(1000));
    }

    @Test
    void calculateLeaguePoints_tierCeiling() {
        // ELO 1199 is one below Apex floor — still Carnivore, LP = 199 clamped to 100
        assertEquals(100, RankTier.calculateLeaguePoints(1199));
    }

    @Test
    void calculateLeaguePoints_meteorUncapped() {
        // ELO 1550 is 150 above Meteor floor (1400) — uncapped
        assertEquals(150, RankTier.calculateLeaguePoints(1550));
    }

    @Test
    void calculateLeaguePoints_hatchlingFloor() {
        // ELO 100 is 100 above Hatchling floor (0) — 100 LP clamped to 100
        assertEquals(100, RankTier.calculateLeaguePoints(100));
    }

    @Test
    void calculateLeaguePoints_notNegative() {
        // Edge: ELO at RATING_FLOOR (100) should never produce negative LP
        assertEquals(0, RankTier.calculateLeaguePoints(0));
    }
}
