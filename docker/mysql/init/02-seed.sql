-- 아티스트 1만, 곡 100만. 재귀 CTE로 생성한다. 초기 기동 시 1~2분 소요.
SET SESSION cte_max_recursion_depth = 1000000;

INSERT INTO artist (name)
SELECT CONCAT('artist-', n)
FROM (WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 10000) SELECT n FROM seq) t;

INSERT INTO song (title, artist_id, play_count, like_count)
SELECT CONCAT('song-', n),
       1 + (n % 10000),
       -- 상위 소수 곡에 재생수가 몰리는 파레토 분포 흉내
       FLOOR(POW(RAND(), 3) * 10000000),
       FLOOR(RAND() * 10000)
FROM (WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 1000000) SELECT n FROM seq) t;
