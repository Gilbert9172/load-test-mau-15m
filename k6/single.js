// 단일 API 집중 부하. 가설 2(차트), 5(좋아요), 6(재생) 등에서 사용.
// 실행: k6 run -e API=chart -e TARGET_RPS=4800 k6/single.js
import http from 'k6/http';
import { check } from 'k6';
import { BASE, hotSongId, userId } from './lib.js';

const API = __ENV.API || 'chart';
const TARGET_RPS = Number(__ENV.TARGET_RPS || 1000);
const DURATION = __ENV.DURATION || '3m';
const HOT_SONG = Number(__ENV.HOT_SONG || 1);   // 가설 5: 특정 곡 1개에 집중
const HOT_RATIO = Number(__ENV.HOT_RATIO || 0.5);

export const options = {
  scenarios: {
    single: {
      executor: 'constant-arrival-rate',
      rate: TARGET_RPS, timeUnit: '1s', duration: DURATION,
      preAllocatedVUs: Math.ceil(TARGET_RPS / 5), maxVUs: TARGET_RPS,
    },
  },
  summaryTrendStats: ['avg', 'p(50)', 'p(95)', 'p(99)', 'max'],
};

const headers = { 'Content-Type': 'application/json' };
const pick = () => (Math.random() < HOT_RATIO ? HOT_SONG : hotSongId());

export default function () {
  let res;
  switch (API) {
    case 'list':   res = http.get(`${BASE}/api/songs?page=${Math.floor(Math.random() * 100)}&size=20&nPlusOne=${__ENV.N_PLUS_ONE || 'false'}`); break;
    case 'detail': res = http.get(`${BASE}/api/songs/${pick()}`); break;
    case 'chart':  res = http.get(`${BASE}/api/chart/top100`); break;
    case 'play':   res = http.post(`${BASE}/api/plays`, JSON.stringify({ songId: pick(), userId: userId() }), { headers }); break;
    case 'like':   res = http.post(`${BASE}/api/songs/${pick()}/like`, null); break;
  }
  check(res, { '2xx': (r) => r.status >= 200 && r.status < 300 });
}
