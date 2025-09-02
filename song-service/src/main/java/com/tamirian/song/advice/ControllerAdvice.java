package com.tamirian.song.advice;

import com.tamirian.song.exception.BadRequestException;
import com.tamirian.song.exception.SongMetadataConflictException;
import com.tamirian.song.exception.SongMetadataNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseWithDetails> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorResponseWithDetails errorResponse = new ErrorResponseWithDetails("Validation error", details, "400");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "400");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(SongMetadataNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(SongMetadataNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(SongMetadataConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(SongMetadataConflictException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "409");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}