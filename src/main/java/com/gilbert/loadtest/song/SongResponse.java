package com.gilbert.loadtest.song;

public record SongResponse(Long id, String title, String artist, long playCount, long likeCount) {
    public static SongResponse from(Song s) {
        return new SongResponse(s.getId(), s.getTitle(), s.getArtist().getName(), s.getPlayCount(), s.getLikeCount());
    }
}
