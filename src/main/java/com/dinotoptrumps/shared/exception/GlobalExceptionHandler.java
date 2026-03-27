package com.dinotoptrumps.shared.exception;

import com.dinotoptrumps.auth.domain.exception.InvalidCredentialsException;
import com.dinotoptrumps.auth.domain.exception.InvalidPasswordException;
import com.dinotoptrumps.auth.domain.exception.UserAlreadyExistsException;
import com.dinotoptrumps.auth.domain.exception.UserNotFoundException;
import com.dinotoptrumps.game.domain.exception.GameNotFoundException;
import com.dinotoptrumps.game.domain.exception.InvalidGameStateException;
import com.dinotoptrumps.game.domain.exception.InvalidStatException;
import com.dinotoptrumps.game.domain.exception.NotYourTurnException;
import com.dinotoptrumps.social.domain.exception.CannotFriendYourselfException;
import com.dinotoptrumps.social.domain.exception.FriendRequestAlreadyExistsException;
import com.dinotoptrumps.social.domain.exception.FriendshipNotFoundException;
import com.dinotoptrumps.social.domain.exception.GameInviteExpiredException;
import com.dinotoptrumps.social.domain.exception.GameInviteNotFoundException;
import com.dinotoptrumps.social.domain.exception.NotFriendsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return buildProblem(HttpStatus.CONFLICT, "User Already Exists", ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildProblem(HttpStatus.UNAUTHORIZED, "Invalid Credentials", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ProblemDetail handleInvalidPassword(InvalidPasswordException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Invalid Password", ex.getMessage());
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ProblemDetail handleGameNotFound(GameNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, "Game Not Found", ex.getMessage());
    }

    @ExceptionHandler(NotYourTurnException.class)
    public ProblemDetail handleNotYourTurn(NotYourTurnException ex) {
        return buildProblem(HttpStatus.FORBIDDEN, "Not Your Turn", ex.getMessage());
    }

    @ExceptionHandler(InvalidStatException.class)
    public ProblemDetail handleInvalidStat(InvalidStatException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Invalid Stat", ex.getMessage());
    }

    @ExceptionHandler(InvalidGameStateException.class)
    public ProblemDetail handleInvalidGameState(InvalidGameStateException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Invalid Game State", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        return buildProblem(HttpStatus.BAD_REQUEST, "Validation Error", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableMessage(HttpMessageNotReadableException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Malformed Request",
                "Invalid request body: " + ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(CannotFriendYourselfException.class)
    public ProblemDetail handleCannotFriendYourself(CannotFriendYourselfException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Cannot Friend Yourself", ex.getMessage());
    }

    @ExceptionHandler(FriendRequestAlreadyExistsException.class)
    public ProblemDetail handleFriendRequestAlreadyExists(FriendRequestAlreadyExistsException ex) {
        return buildProblem(HttpStatus.CONFLICT, "Friend Request Already Exists", ex.getMessage());
    }

    @ExceptionHandler(FriendshipNotFoundException.class)
    public ProblemDetail handleFriendshipNotFound(FriendshipNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, "Friendship Not Found", ex.getMessage());
    }

    @ExceptionHandler(NotFriendsException.class)
    public ProblemDetail handleNotFriends(NotFriendsException ex) {
        return buildProblem(HttpStatus.FORBIDDEN, "Not Friends", ex.getMessage());
    }

    @ExceptionHandler(GameInviteNotFoundException.class)
    public ProblemDetail handleGameInviteNotFound(GameInviteNotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, "Game Invite Not Found", ex.getMessage());
    }

    @ExceptionHandler(GameInviteExpiredException.class)
    public ProblemDetail handleGameInviteExpired(GameInviteExpiredException ex) {
        return buildProblem(HttpStatus.GONE, "Game Invite Expired", ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        return buildProblem(HttpStatus.CONFLICT, "Conflict",
                "Game state was modified by another request. Please retry.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        return buildProblem(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error",
                "An unexpected error occurred");
    }

    private ProblemDetail buildProblem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("about:blank"));
        return problem;
    }
}
