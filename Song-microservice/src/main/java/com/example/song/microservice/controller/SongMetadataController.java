package com.example.song.microservice.controller;

import com.example.song.microservice.dto.SongMetadataDto;
import com.example.song.microservice.service.SongMetadataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
public class SongMetadataController {

    private final SongMetadataService songService;

    public SongMetadataController(SongMetadataService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> createSong(@Valid @RequestBody SongMetadataDto dto) {
        return ResponseEntity.ok(songService.createSongMetadata(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongMetadataDto> getSong(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongMetadataById(id));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Long>>> deleteSongs(@RequestParam("id") String ids) {
        return ResponseEntity.ok(Map.of("ids",songService.deleteSongsMetadataByIds(ids)));
    }
}
