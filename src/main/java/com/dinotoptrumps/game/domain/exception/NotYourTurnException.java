package com.dinotoptrumps.game.domain.exception;

public class NotYourTurnException extends RuntimeException {

    public NotYourTurnException(String message) {
        super(message);
    }
}
