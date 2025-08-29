package com.example.song.microservice.exception;

public class SongMetadataNotFoundException extends RuntimeException{
    public SongMetadataNotFoundException(String message) {
        super(message);
    }
}
