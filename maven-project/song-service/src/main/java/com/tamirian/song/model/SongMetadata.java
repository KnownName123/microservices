package com.tamirian.song.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Duration;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SongMetadata {
    @Id
    private Long id;

    private String name;

    private String artist;

    private String album;

    private String duration;

    private Integer year;
}
