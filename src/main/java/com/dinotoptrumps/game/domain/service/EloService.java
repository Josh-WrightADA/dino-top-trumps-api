package com.dinotoptrumps.game.domain.service;

public class EloService {

    private static final int K_FACTOR = 32;
    private static final int RATING_FLOOR = 100;

    public int calculateNewRating(int playerRating, int opponentRating, double actualScore) {
        double expectedScore = calculateExpectedScore(playerRating, opponentRating);
        int newRating = (int) Math.round(playerRating + K_FACTOR * (actualScore - expectedScore));
        return Math.max(newRating, RATING_FLOOR);
    }

    public double calculateExpectedScore(int playerRating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, (opponentRating - playerRating) / 400.0));
    }

    public int[] updateRatings(int winnerRating, int loserRating) {
        int newWinnerRating = calculateNewRating(winnerRating, loserRating, 1.0);
        int newLoserRating = calculateNewRating(loserRating, winnerRating, 0.0);
        return new int[]{newWinnerRating, newLoserRating};
    }
}
