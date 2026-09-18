package com.gilbert.loadtest.play;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** 재생 기록. 가설 6(쓰기 병목)의 대상. 하루 2억 행이 쌓이는 테이블이라는 가정. */
@Entity
@Table(name = "play_log")
public class PlayLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "song_id", nullable = false)
    private Long songId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "played_at", nullable = false)
    private LocalDateTime playedAt;

    protected PlayLog() {}

    public PlayLog(Long songId, Long userId) {
        this.songId = songId;
        this.userId = userId;
        this.playedAt = LocalDateTime.now();
    }
}
