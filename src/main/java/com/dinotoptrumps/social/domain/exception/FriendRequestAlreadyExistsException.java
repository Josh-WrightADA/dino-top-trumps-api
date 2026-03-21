package com.dinotoptrumps.social.domain.exception;

public class FriendRequestAlreadyExistsException extends RuntimeException {
    public FriendRequestAlreadyExistsException(String message) {
        super(message);
    }
}
