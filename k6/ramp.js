// 가설 1(베이스라인): RPS를 단계적으로 올리며 수치가 흔들리는 지점을 찾는다.
// 실행: k6 run k6/ramp.js
import http from 'k6/http';
import { check } from 'k6';
import { BASE, hotSongId } from './lib.js';

export const options = {
  scenarios: {
    ramp: {
      executor: 'ramping-arrival-rate',
      startRate: 500,
      timeUnit: '1s',
      preAllocatedVUs: 200,
      maxVUs: 4000,
      stages: [
        { target: 1000, duration: '1m' },
        { target: 2000, duration: '1m' },
        { target: 4000, duration: '1m' },
        { target: 8000, duration: '1m' },
        { target: 12000, duration: '1m' },
        { target: 16000, duration: '1m' },
      ],
    },
  },
  summaryTrendStats: ['avg', 'p(50)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  const res = http.get(`${BASE}/api/songs/${hotSongId()}`);
  check(res, { 'status 200': (r) => r.status === 200 });
}
