package com.beloboki.exception;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception e) {
        log.error("Handle exception", e);
        ProblemDetail responseError = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        responseError.setTitle("Internal server error");
        responseError.setDetail(e.getMessage());
        responseError.setProperty("errorTime", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(Exception e) {
        log.error("Handle entity not found exception", e);
        ProblemDetail responseError = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        responseError.setTitle("Entity not found");
        responseError.setDetail(e.getMessage());
        responseError.setProperty("errorTime", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseError);
    }

    @ExceptionHandler({
        IllegalArgumentException.class,
        IllegalStateException.class,
        MethodArgumentNotValidException.class,
        CardLimitException.class
    })
    public ResponseEntity<ProblemDetail> handleBadRequest(Exception e) {
        log.error("Handle IllegalArgumentException", e);
        ProblemDetail responseError = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        responseError.setTitle("Bad request");
        responseError.setDetail(e.getMessage());
        responseError.setProperty("errorTime", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAuthorizationDenied(AuthorizationDeniedException e) {
        log.error("Handle authorization exception", e);
        ProblemDetail responseError = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        responseError.setTitle("Unauthorized");
        responseError.setDetail(e.getMessage());
        responseError.setProperty("errorTime", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseError);
    }
}
