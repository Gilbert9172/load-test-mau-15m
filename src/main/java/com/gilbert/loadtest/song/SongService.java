package com.gilbert.loadtest.song;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    /** 곡 목록. 가설 4에서 nPlusOne=true 로 호출하면 지연 로딩 경로를 탄다. */
    public List<SongResponse> list(int page, int size, boolean nPlusOne) {
        List<Song> songs = nPlusOne
                ? songRepository.findAll(PageRequest.of(page, size)).getContent()
                : songRepository.findPageWithArtist(PageRequest.of(page, size));
        return songs.stream().map(SongResponse::from).toList();
    }

    /** 곡 상세. 가설 2·3에서 캐시와 커넥션 풀 실험의 대상. */
    public SongResponse detail(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("song not found: " + id));
        return SongResponse.from(song);
    }
}
