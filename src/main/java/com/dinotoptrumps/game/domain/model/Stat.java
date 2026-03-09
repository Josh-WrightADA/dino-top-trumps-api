package com.dinotoptrumps.game.domain.model;

public enum Stat {
    HEIGHT,
    WEIGHT,
    INTELLIGENCE,
    SPEED,
    STRENGTH;

    public int getValueFromCard(Card card) {
        return switch (this) {
            case HEIGHT -> card.getHeight();
            case WEIGHT -> card.getWeight();
            case INTELLIGENCE -> card.getIntelligence();
            case SPEED -> card.getSpeed();
            case STRENGTH -> card.getStrength();
        };
    }
}
