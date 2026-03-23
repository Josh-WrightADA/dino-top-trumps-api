package com.dinotoptrumps.game.domain.model;

public enum GameEndReason {
    NORMAL,     // One player collected all cards
    FORFEIT,    // Player voluntarily forfeited
    TIMEOUT     // Turn timer expired, auto-forfeit
}
