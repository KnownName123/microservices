package com.example.resource.microservice.dto;

public record SongMetadataDto(
        String id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) {}
