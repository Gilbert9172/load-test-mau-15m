package com.gilbert.loadtest.song;

import jakarta.persistence.*;

@Entity
@Table(name = "song")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    // 가설 4(N+1) 실험을 위해 기본은 LAZY. 실험에서 fetch join / EAGER / DTO 프로젝션으로 전환한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(name = "play_count", nullable = false)
    private long playCount;

    @Column(name = "like_count", nullable = false)
    private long likeCount;

    protected Song() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Artist getArtist() { return artist; }
    public long getPlayCount() { return playCount; }
    public long getLikeCount() { return likeCount; }
}
