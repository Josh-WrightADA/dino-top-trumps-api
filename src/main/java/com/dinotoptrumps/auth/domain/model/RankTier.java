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
}
