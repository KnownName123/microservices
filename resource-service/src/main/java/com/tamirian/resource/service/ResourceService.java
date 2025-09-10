package com.tamirian.resource.service;

import com.tamirian.resource.dto.SongMetadataDto;
import com.tamirian.resource.exception.BadRequestException;
import com.tamirian.resource.exception.ResourceNotFoundException;
import com.tamirian.resource.model.Resource;
import com.tamirian.resource.repository.ResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final Tika tika = new Tika();
    private final ResourceRepository repository;
    private final RestTemplate restTemplate;

    @Value("${SONG_SERVICE_URL:http://song-service/songs}")
    private String songServiceUrl;

    @Transactional
    public Long upload(byte[] mp3) throws IOException, SAXException, TikaException {
        validateMp3(mp3);
        Resource resource = repository.save(createResource(mp3));
        SongMetadataDto metadata = extractMetadata(mp3, resource.getId());
        return sendMetadata(metadata);
    }

    public byte[] getResource(String idStr) {
        Long id = parsePositiveId(idStr);
        return repository.findById(id)
                .map(Resource::getData)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID " + id + " not found"));
    }

    @Transactional
    public List<Long> delete(String csv) {
        List<Long> ids = parseCsvIds(csv);
        if (ids.isEmpty()) return Collections.emptyList();

        List<Long> existingIds = repository.findAllById(ids).stream()
                .map(Resource::getId)
                .toList();

        if (existingIds.isEmpty()) return Collections.emptyList();

        deleteFromSongService(csv);
        repository.deleteAllById(existingIds);

        return existingIds;
    }

    public SongMetadataDto extractMetadata(byte[] mp3, Long resourceId) throws IOException, SAXException, TikaException {
        Metadata metadata = new Metadata();
        BodyContentHandler handler = new BodyContentHandler();
        Parser parser = new Mp3Parser();
        try (InputStream input = new ByteArrayInputStream(mp3)) {
            parser.parse(input, handler, metadata, new org.apache.tika.parser.ParseContext());
        }

        return new SongMetadataDto(
                resourceId.toString(),
                truncateOrDefault(metadata.get(TikaCoreProperties.TITLE), "Unknown Title", 100),
                truncateOrDefault(metadata.get(XMPDM.ARTIST), "Unknown Artist", 100),
                truncateOrDefault(metadata.get(XMPDM.ALBUM), "Unknown Album", 100),
                formatDuration(metadata.get(XMPDM.DURATION)),
                validateYear(metadata.get(XMPDM.RELEASE_DATE))
        );
    }

    private void validateMp3(byte[] mp3) {
        if (!"audio/mpeg".equalsIgnoreCase(tika.detect(mp3))) {
            throw new BadRequestException("Invalid MP3 file");
        }
    }

    private Resource createResource(byte[] mp3) {
        Resource resource = new Resource();
        resource.setData(mp3);
        return resource;
    }

    private Long sendMetadata(SongMetadataDto metadata) {
        HttpEntity<SongMetadataDto> entity = new HttpEntity<>(metadata, createJsonHeaders());
        ResponseEntity<Map<String, Integer>> response = restTemplate.exchange(
                songServiceUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<>() {}
        );
        Map<String, Integer> body = response.getBody();
        if (body == null || !body.containsKey("id")) {
            throw new RuntimeException("Song Service returned invalid response");
        }
        return body.get("id").longValue();
    }

    private void deleteFromSongService(String csv) {
        HttpEntity<Void> entity = new HttpEntity<>(createJsonHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(
                songServiceUrl + "?id=" + csv, HttpMethod.DELETE, entity, Map.class
        );
        Map<String, List<Long>> body = response.getBody();
        if (body == null || !body.containsKey("ids")) {
            throw new RuntimeException("Song Service returned invalid response");
        }
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private Long parsePositiveId(String idStr) {
        try {
            if (idStr.contains(".")) throw new NumberFormatException();
            long id = Long.parseLong(idStr);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid ID format: '" + idStr + "'. Must be positive integer");
        }
    }

    private List<Long> parseCsvIds(String csv) {
        if (csv == null || csv.isBlank()) throw new BadRequestException("CSV string is empty or null");
        if (csv.length() > 200) throw new BadRequestException("CSV string exceeds max length 200");

        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::parsePositiveId)
                .distinct()
                .toList();
    }

    private String truncateOrDefault(String value, String defaultValue, int maxLength) {
        value = (value == null || value.isBlank()) ? defaultValue : value;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    private String validateYear(String year) {
        if (year != null && year.matches("^(19|20)\\d{2}$")) return year;
        return "1900";
    }

    private String formatDuration(String durationStr) {
        if (durationStr == null) return "00:00";
        try {
            long seconds = Long.parseLong(durationStr) / 1000;
            return String.format("%02d:%02d", seconds / 60, seconds % 60);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }
}