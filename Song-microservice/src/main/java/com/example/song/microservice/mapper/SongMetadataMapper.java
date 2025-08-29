package com.example.song.microservice.mapper;

import com.example.song.microservice.dto.SongMetadataDto;
import com.example.song.microservice.model.SongMetadata;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SongMetadataMapper {

    public SongMetadata toEntity(SongMetadataDto dto){
        return new SongMetadata(
                Long.valueOf(dto.id()),
                dto.name(),
                dto.artist(),
                dto.album(),
                (dto.duration()),
                Integer.parseInt(dto.year()));
    }

    public SongMetadataDto toDto(SongMetadata entity){
        return new SongMetadataDto(
                entity.getId().toString(),
                entity.getName(),
                entity.getArtist(),
                entity.getAlbum(),
                entity.getDuration(),
                entity.getYear().toString());
    }
}
