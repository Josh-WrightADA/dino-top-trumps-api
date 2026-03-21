package com.dinotoptrumps.social.domain.exception;

public class GameInviteExpiredException extends RuntimeException {
    public GameInviteExpiredException(String message) {
        super(message);
    }
}
