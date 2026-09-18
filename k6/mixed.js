// 용량 산정의 트래픽 믹스: 목록 30 / 상세 25 / 차트 30 / 재생 10 / 좋아요 5
// 실행: k6 run -e TARGET_RPS=2000 -e DURATION=3m k6/mixed.js
import http from 'k6/http';
import { check } from 'k6';
import { BASE, hotSongId, userId, thresholds } from './lib.js';

const TARGET_RPS = Number(__ENV.TARGET_RPS || 1000);
const DURATION = __ENV.DURATION || '2m';

export const options = {
  scenarios: {
    mixed: {
      executor: 'constant-arrival-rate',
      rate: TARGET_RPS,
      timeUnit: '1s',
      duration: DURATION,
      preAllocatedVUs: Math.ceil(TARGET_RPS / 5),
      maxVUs: TARGET_RPS,
    },
  },
  thresholds,
  summaryTrendStats: ['avg', 'p(50)', 'p(95)', 'p(99)', 'max'],
};

const headers = { 'Content-Type': 'application/json' };

export default function () {
  const r = Math.random();
  let res;
  if (r < 0.30) {
    res = http.get(`${BASE}/api/songs?page=${Math.floor(Math.random() * 100)}&size=20`, { tags: { api: 'list' } });
  } else if (r < 0.55) {
    res = http.get(`${BASE}/api/songs/${hotSongId()}`, { tags: { api: 'detail' } });
  } else if (r < 0.85) {
    res = http.get(`${BASE}/api/chart/top100`, { tags: { api: 'chart' } });
  } else if (r < 0.95) {
    res = http.post(`${BASE}/api/plays`, JSON.stringify({ songId: hotSongId(), userId: userId() }), { headers, tags: { api: 'play' } });
  } else {
    res = http.post(`${BASE}/api/songs/${hotSongId()}/like`, null, { tags: { api: 'like' } });
  }
  check(res, { 'status 2xx': (r) => r.status >= 200 && r.status < 300 });
}
