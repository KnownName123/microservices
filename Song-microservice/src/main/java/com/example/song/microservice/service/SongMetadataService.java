package com.example.song.microservice.service;

import com.example.song.microservice.dto.SongMetadataDto;
import com.example.song.microservice.exception.BadRequestException;
import com.example.song.microservice.exception.SongMetadataConflictException;
import com.example.song.microservice.exception.SongMetadataNotFoundException;
import com.example.song.microservice.mapper.SongMetadataMapper;
import com.example.song.microservice.model.SongMetadata;
import com.example.song.microservice.repository.SongMetadataRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Long.valueOf;

@Service
public class SongMetadataService {

    public SongMetadataRepository songMetadataRepository;
    public SongMetadataMapper songMetadataMapper;

    @Autowired
    public SongMetadataService(SongMetadataRepository songMetadataRepository, SongMetadataMapper songMetadataMapper){
        this.songMetadataRepository = songMetadataRepository;
        this.songMetadataMapper = songMetadataMapper;
    }

    public Map<String, Long> createSongMetadata(SongMetadataDto dto){
        Long id = Long.valueOf(dto.id());
        if (songMetadataRepository.existsById(id)){
            throw new SongMetadataConflictException(
                    "Metadata for resource ID " + id + " already exists."
            );
        }

        songMetadataRepository.save(songMetadataMapper.toEntity(dto));

        Map<String, Long> map = new HashMap<>();
        map.put("id", id);
        return map;
    }

    public SongMetadataDto getSongMetadataById(Long resourceId){
        return songMetadataMapper.toDto(
                songMetadataRepository.findById(resourceId)
                        .orElseThrow(() ->
                                new SongMetadataNotFoundException(
                                        "Song metadata with ID " + resourceId + " does not exist."
                                )
                        )
        );
    }

    @Transactional
    public List<Long> deleteSongsMetadataByIds(String csv) {
        if (csv == null || csv.isBlank()) {
            throw new BadRequestException("CSV string must not be empty");
        }
        if (csv.length() > 200) {
            throw new BadRequestException(
                    "CSV string is too long: received " + csv.length() + " characters, maximum allowed is 200"
            );
        }

        List<Long> ids = Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::parseIdStrict)
                .distinct()
                .toList();

        if (ids.isEmpty()) {
            return List.of();
        }

        List<Long> existingIds = songMetadataRepository.findAllById(ids).stream()
                .map(SongMetadata::getId)
                .toList();

        if (existingIds.isEmpty()) {
            return List.of();
        }

        songMetadataRepository.deleteAllById(existingIds);

        return existingIds;
    }

    private Long parseIdStrict(String s) {
        try {
            long v = Long.parseLong(s);
            if (v <= 0) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid ID format: '" + s + "'. Only positive integers are allowed");
        }
    }

}