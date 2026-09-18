package com.gilbert.loadtest.like;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 좋아요. song.like_count 를 직접 UPDATE 하므로 특정 곡에 몰리면 같은 행의 락을 기다린다.
 * 가설 5(핫 로우)의 대상. 실험에서 카운터 분리, Redis INCR + 비동기 반영으로 바꿔 비교한다.
 */
@Service
public class LikeService {

    private final EntityManager em;

    public LikeService(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void like(Long songId) {
        em.createNativeQuery("update song set like_count = like_count + 1 where id = :id")
                .setParameter("id", songId)
                .executeUpdate();
    }
}
