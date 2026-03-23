package com.dinotoptrumps.game.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EloServiceTest {

    private EloService eloService;

    @BeforeEach
    void setUp() {
        eloService = new EloService();
    }

    @Test
    void equalRatings_shouldReturn0Point5() {
        double expected = eloService.calculateExpectedScore(1000, 1000);
        assertEquals(0.5, expected, 0.001);
    }

    @Test
    void higherRatedPlayer_shouldExpectHigherScore() {
        double expected = eloService.calculateExpectedScore(1200, 1000);
        assertTrue(expected > 0.5);
    }

    @Test
    void lowerRatedPlayer_shouldExpectLowerScore() {
        double expected = eloService.calculateExpectedScore(1000, 1200);
        assertTrue(expected < 0.5);
    }

    @Test
    void expectedWin_shouldGainSmallRating() {
        int newRating = eloService.calculateNewRating(1200, 1000, 1.0);
        assertTrue(newRating > 1200 && newRating < 1210);
    }

    @Test
    void upsetWin_shouldGainLargeRating() {
        int expectedWinGain = eloService.calculateNewRating(1200, 1000, 1.0) - 1200;
        int upsetWinGain = eloService.calculateNewRating(1000, 1200, 1.0) - 1000;
        assertTrue(upsetWinGain > expectedWinGain);
    }

    @Test
    void loss_shouldDecreaseRating() {
        int newRating = eloService.calculateNewRating(1000, 1000, 0.0);
        assertTrue(newRating < 1000);
    }

    @Test
    void ratingFloor_shouldNotGoBelowMinimum() {
        int newRating = eloService.calculateNewRating(100, 2000, 0.0);
        assertTrue(newRating >= 100);
    }

    @Test
    void updateRatings_winnerGainsWhatLoserLoses() {
        int[] ratings = eloService.updateRatings(1000, 1000);
        int winnerNew = ratings[0];
        int loserNew = ratings[1];
        assertTrue(winnerNew > 1000 && loserNew < 1000);
        assertEquals(winnerNew - 1000, 1000 - loserNew);
    }

    @Test
    void updateRatings_returnsArrayOfTwoRatings() {
        int[] ratings = eloService.updateRatings(1500, 1200);
        assertEquals(2, ratings.length);
    }

    // --- Dynamic K-factor tests ---

    @Test
    void getKFactor_placement_returns64() {
        // gamesPlayed=5 → placement phase, K=64
        // Win against equal opponent (expected=0.5) → gain = round(64 * 0.5) = 32
        int placementGain = eloService.calculateNewRating(1000, 1000, 1.0, 5) - 1000;
        assertEquals(32, placementGain);
    }

    @Test
    void getKFactor_calibrating_returns48() {
        // gamesPlayed=15 → calibrating phase, K=48
        // Win against equal opponent → gain = round(48 * 0.5) = 24
        int calibratingGain = eloService.calculateNewRating(1000, 1000, 1.0, 15) - 1000;
        assertEquals(24, calibratingGain);
    }

    @Test
    void getKFactor_stable_returns32() {
        // gamesPlayed=50 → stable phase, K=32
        // Win against equal opponent → gain = round(32 * 0.5) = 16
        int stableGain = eloService.calculateNewRating(1000, 1000, 1.0, 50) - 1000;
        assertEquals(16, stableGain);
    }

    @Test
    void updateRatings_placementPhase_largerSwings() {
        // Placement games should produce bigger rating changes than stable games
        int[] placementRatings = eloService.updateRatings(1000, 1000, 5, 5);
        int[] stableRatings = eloService.updateRatings(1000, 1000, 30, 30);

        int placementSwing = placementRatings[0] - 1000;
        int stableSwing = stableRatings[0] - 1000;

        assertTrue(placementSwing > stableSwing,
                "Placement phase should produce larger rating swings than stable phase");
    }

    @Test
    void updateRatings_withGamesPlayed_delegatesCorrectly() {
        // 4-param version with equal ratings and winner in placement → winner gains 32 LP
        int[] ratings = eloService.updateRatings(1000, 1000, 5, 30);
        // winner K=64: gain = 64 * (1 - 0.5) = 32
        // loser K=32: loss = 32 * (0 - 0.5) = -16
        assertEquals(1032, ratings[0], "Winner in placement should gain 32 LP");
        assertEquals(984, ratings[1], "Loser in stable phase should lose 16 LP");
    }
}
