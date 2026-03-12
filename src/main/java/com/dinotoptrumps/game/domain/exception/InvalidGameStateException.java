package com.dinotoptrumps.game.domain.exception;

public class InvalidGameStateException extends RuntimeException {

    public InvalidGameStateException(String message) {
        super(message);
    }
}
