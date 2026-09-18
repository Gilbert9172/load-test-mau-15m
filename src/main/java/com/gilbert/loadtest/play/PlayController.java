package com.gilbert.loadtest.play;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plays")
public class PlayController {

    private final PlayLogRepository playLogRepository;

    public PlayController(PlayLogRepository playLogRepository) {
        this.playLogRepository = playLogRepository;
    }

    public record PlayRequest(Long songId, Long userId) {}

    /** 기본은 동기 INSERT. 가설 6에서 배치/큐로 바꾼다. */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void play(@RequestBody PlayRequest req) {
        playLogRepository.save(new PlayLog(req.songId(), req.userId()));
    }
}
