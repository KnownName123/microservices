package com.tamirian.song.repository;

import com.tamirian.song.model.SongMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SongMetadataRepository extends JpaRepository<SongMetadata, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM SongMetadata s WHERE s.id IN :ids")
    void deleteByIds(List<Long> ids);
}
