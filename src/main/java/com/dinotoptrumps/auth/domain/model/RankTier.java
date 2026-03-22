package com.dinotoptrumps.auth.domain.model;

public enum RankTier {
    HATCHLING,
    HERBIVORE,
    CARNIVORE,
    APEX,
    METEOR;

    public static RankTier fromElo(int elo) {
        if (elo < 800) {
            return HATCHLING;
        } else if (elo < 1000) {
            return HERBIVORE;
        } else if (elo < 1200) {
            return CARNIVORE;
        } else if (elo < 1400) {
            return APEX;
        } else {
            return METEOR;
        }
    }

    public int getFloorElo() {
        return switch (this) {
            case HATCHLING -> 0;
            case HERBIVORE -> 800;
            case CARNIVORE -> 1000;
            case APEX -> 1200;
            case METEOR -> 1400;
        };
    }

    public static int calculateLeaguePoints(int elo) {
        RankTier tier = fromElo(elo);
        int lp = elo - tier.getFloorElo();
        if (tier == METEOR) {
            return Math.max(0, lp);
        }
        return Math.max(0, Math.min(100, lp));
    }
}
