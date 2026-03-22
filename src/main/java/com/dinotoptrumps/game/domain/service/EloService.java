package com.dinotoptrumps.game.domain.service;

public class EloService {

    private static final int RATING_FLOOR = 100;

    public int calculateNewRating(int playerRating, int opponentRating, double actualScore, int gamesPlayed) {
        int kFactor = getKFactor(gamesPlayed);
        double expectedScore = calculateExpectedScore(playerRating, opponentRating);
        int newRating = (int) Math.round(playerRating + kFactor * (actualScore - expectedScore));
        return Math.max(newRating, RATING_FLOOR);
    }

    /**
     * Legacy overload — kept for backward compatibility.
     * Delegates to the gamesPlayed-aware version using stable K-factor (K=32).
     */
    public int calculateNewRating(int playerRating, int opponentRating, double actualScore) {
        return calculateNewRating(playerRating, opponentRating, actualScore, 30);
    }

    public double calculateExpectedScore(int playerRating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, (opponentRating - playerRating) / 400.0));
    }

    public int[] updateRatings(int winnerRating, int loserRating, int winnerGamesPlayed, int loserGamesPlayed) {
        int newWinnerRating = calculateNewRating(winnerRating, loserRating, 1.0, winnerGamesPlayed);
        int newLoserRating = calculateNewRating(loserRating, winnerRating, 0.0, loserGamesPlayed);
        return new int[]{newWinnerRating, newLoserRating};
    }

    /**
     * Legacy overload — kept for backward compatibility.
     * Delegates with stable K-factor (K=32).
     */
    public int[] updateRatings(int winnerRating, int loserRating) {
        return updateRatings(winnerRating, loserRating, 30, 30);
    }

    private int getKFactor(int gamesPlayed) {
        if (gamesPlayed < 10) {
            return 64;
        }
        if (gamesPlayed < 30) {
            return 48;
        }
        return 32;
    }
}
