package com.practice.url_shortner.exception;

import com.practice.url_shortner.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.practice.url_shortner.exception.EmailAlreadyExistsException;
import com.practice.url_shortner.exception.InvalidCredentialsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — URL not found
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(UrlNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "URL_NOT_FOUND",
                ex.getMessage(),
                404
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 410 — URL expired (Gone — more accurate than 404 for expired content)
    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<ErrorResponse> handleUrlExpired(UrlExpiredException ex) {
        ErrorResponse error = new ErrorResponse(
                "URL_EXPIRED",
                ex.getMessage(),
                410
        );
        return new ResponseEntity<>(error, HttpStatus.GONE);
    }

    // 400 — Invalid URL format
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException ex) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_URL",
                ex.getMessage(),
                400
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 409 — Duplicate alias (Conflict)
    @ExceptionHandler(DuplicateAliasException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateAlias(DuplicateAliasException ex) {
        ErrorResponse error = new ErrorResponse(
                "ALIAS_TAKEN",
                ex.getMessage(),
                409
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 429 — Rate limit exceeded
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(RateLimitExceededException ex) {
        ErrorResponse error = new ErrorResponse(
                "RATE_LIMIT_EXCEEDED",
                ex.getMessage(),
                429
        );
        return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
    }

    // 404 — Student not found (keeping for practice endpoints)
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(StudentNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "STUDENT_NOT_FOUND",
                ex.getMessage(),
                404
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 500 — Catch-all for any unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred: " + ex.getMessage(),
                500
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // 409 — Email already registered
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                "EMAIL_ALREADY_EXISTS",
                ex.getMessage(),
                409
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // 401 — Wrong email or password
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                "INVALID_CREDENTIALS",
                ex.getMessage(),
                401
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
}