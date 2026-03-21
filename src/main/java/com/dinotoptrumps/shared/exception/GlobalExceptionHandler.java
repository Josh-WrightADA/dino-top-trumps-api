package com.dinotoptrumps.shared.exception;

import com.dinotoptrumps.auth.domain.exception.InvalidCredentialsException;
import com.dinotoptrumps.auth.domain.exception.UserAlreadyExistsException;
import com.dinotoptrumps.game.domain.exception.GameNotFoundException;
import com.dinotoptrumps.game.domain.exception.InvalidGameStateException;
import com.dinotoptrumps.game.domain.exception.InvalidStatException;
import com.dinotoptrumps.game.domain.exception.NotYourTurnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
