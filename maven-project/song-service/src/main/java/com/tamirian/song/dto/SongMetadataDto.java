package com.tamirian.song.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SongMetadataDto(
        @NotBlank(message = "ID is required") @Pattern(regexp = "^[1-9]\\d*$", message = "ID must be a positive integer")
        String id,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 100) String artist,
        @NotBlank @Size(max = 100) String album,
        @Pattern(regexp = "^[0-5][0-9]:[0-5][0-9]$", message = "Duration must be in mm:ss format with leading zeros")
        String duration,
        @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be between 1900 and 2099")
        String year
) {}
