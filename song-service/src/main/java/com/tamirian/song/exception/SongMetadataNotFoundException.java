package com.tamirian.song.exception;

public class SongMetadataNotFoundException extends RuntimeException{
    public SongMetadataNotFoundException(String message) {
        super(message);
    }
}
