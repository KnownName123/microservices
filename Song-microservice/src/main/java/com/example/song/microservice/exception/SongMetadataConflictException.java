package com.example.song.microservice.exception;

public class SongMetadataConflictException extends RuntimeException {
    public SongMetadataConflictException(String message) {
        super(message);
    }
}
