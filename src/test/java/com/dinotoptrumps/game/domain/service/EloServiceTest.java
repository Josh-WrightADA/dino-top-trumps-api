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
}
