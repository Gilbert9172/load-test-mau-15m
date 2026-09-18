package com.gilbert.loadtest.chart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gilbert.loadtest.song.SongRepository;
import com.gilbert.loadtest.song.SongResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * 인기 차트. 전 사용자가 같은 키를 보므로 가설 2(캐시 스탬피드)의 실험 대상.
 * 기본 구현은 의도적으로 단순한 cache-aside. TTL 만료 순간 모든 요청이 DB로 간다.
 * 실험에서 락, 조기 갱신(early refresh), 백그라운드 갱신으로 바꿔가며 비교한다.
 */
@Service
public class ChartService {

    private static final String KEY = "chart:top100";

    private final SongRepository songRepository;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public ChartService(SongRepository songRepository,
                        StringRedisTemplate redis,
                        ObjectMapper objectMapper,
                        @Value("${loadtest.chart.ttl-seconds:60}") long ttlSeconds) {
        this.songRepository = songRepository;
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public List<SongResponse> top100() {
        try {
            String cached = redis.opsForValue().get(KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<>() {});
            }
            List<SongResponse> fresh = loadFromDb();
            redis.opsForValue().set(KEY, objectMapper.writeValueAsString(fresh), ttl);
            return fresh;
        } catch (Exception e) {
            throw new IllegalStateException("chart load failed", e);
        }
    }

    private List<SongResponse> loadFromDb() {
        return songRepository.findTopByPlayCount(PageRequest.of(0, 100))
                .stream().map(SongResponse::from).toList();
    }
}
