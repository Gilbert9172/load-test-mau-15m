package com.gilbert.loadtest.song;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    // 가설 4: 기본 findAll(Pageable)은 artist 접근 시 N+1. 아래 fetch join 버전과 비교한다.
    @Query("select s from Song s join fetch s.artist order by s.id desc")
    List<Song> findPageWithArtist(Pageable pageable);

    @Query("select s from Song s join fetch s.artist order by s.playCount desc")
    List<Song> findTopByPlayCount(Pageable pageable);
}
