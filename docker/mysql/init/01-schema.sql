CREATE TABLE artist (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE song (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  title      VARCHAR(200) NOT NULL,
  artist_id  BIGINT NOT NULL,
  play_count BIGINT NOT NULL DEFAULT 0,
  like_count BIGINT NOT NULL DEFAULT 0,
  KEY idx_song_play_count (play_count DESC),
  CONSTRAINT fk_song_artist FOREIGN KEY (artist_id) REFERENCES artist(id)
) ENGINE=InnoDB;

CREATE TABLE play_log (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  song_id   BIGINT NOT NULL,
  user_id   BIGINT NOT NULL,
  played_at DATETIME(3) NOT NULL,
  KEY idx_play_log_song (song_id),
  KEY idx_play_log_played_at (played_at)
) ENGINE=InnoDB;
