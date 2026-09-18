export const BASE = __ENV.BASE_URL || 'http://localhost:8080';
export const SONG_COUNT = 1_000_000;

// 파레토: 소수의 인기 곡에 요청이 몰린다. 가설 2·5의 핫 키/핫 로우 전제.
export function hotSongId() {
  const r = Math.random();
  if (r < 0.5) return 1 + Math.floor(Math.random() * 100);        // 50%는 상위 100곡
  if (r < 0.8) return 1 + Math.floor(Math.random() * 10_000);     // 30%는 상위 1만곡
  return 1 + Math.floor(Math.random() * SONG_COUNT);
}

export function userId() {
  return 1 + Math.floor(Math.random() * 3_750_000); // DAU
}

export const thresholds = {
  http_req_failed: ['rate<0.01'],
  http_req_duration: ['p(99)<500'],
};
