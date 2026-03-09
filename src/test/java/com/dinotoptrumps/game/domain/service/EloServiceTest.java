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

    // --- calculateExpectedScore tests ---

    @Test
    void equalRatings_shouldReturn0Point5() {
        double expected = eloService.calculateExpectedScore(1000, 1000);
        assertEquals(0.5, expected, 0.001);
    }

    @Test
    void higherRatedPlayer_shouldExpectHigherScore() {
        double expected = eloService.calculateExpectedScore(1200, 1000);
        // TODO: What should a 1200-rated player expect against a 1000-rated player?
        // The expected score should be greater than 0.5 - assert the approximate value
    }

    @Test
    void lowerRatedPlayer_shouldExpectLowerScore() {
        // TODO: Test that a 1000-rated player vs a 1200-rated player expects less than 0.5
    }

    // --- calculateNewRating tests ---

    @Test
    void expectedWin_shouldGainSmallRating() {
        // A 1200-rated player beats a 1000-rated player (expected outcome)
        int newRating = eloService.calculateNewRating(1200, 1000, 1.0);
        // TODO: Assert the new rating is higher than 1200, but not by much
        // Hint: an expected win gains fewer points than an upset
    }

    @Test
    void upsetWin_shouldGainLargeRating() {
        // A 1000-rated player beats a 1200-rated player (upset!)
        int newRating = eloService.calculateNewRating(1000, 1200, 1.0);
        // TODO: Assert the new rating gain is larger than the expected win case above
    }

    @Test
    void loss_shouldDecreaseRating() {
        int newRating = eloService.calculateNewRating(1000, 1000, 0.0);
        // TODO: Assert rating decreased from 1000
    }

    @Test
    void ratingFloor_shouldNotGoBelowMinimum() {
        // A very low-rated player loses - rating should not go below 100
        int newRating = eloService.calculateNewRating(100, 2000, 0.0);
        // TODO: Assert the floor is respected
    }

    // --- updateRatings tests ---

    @Test
    void updateRatings_winnerGainsWhatLoserLoses() {
        int[] ratings = eloService.updateRatings(1000, 1000);
        int winnerNew = ratings[0];
        int loserNew = ratings[1];
        // TODO: For equal starting ratings, the gain and loss should be symmetric
        // Assert winner > 1000, loser < 1000, and the change amounts are equal
    }

    @Test
    void updateRatings_returnsArrayOfTwoRatings() {
        int[] ratings = eloService.updateRatings(1500, 1200);
        assertEquals(2, ratings.length);
    }
}
