# load-test-mau-15m

MAU 1500만 서비스가 받는 부하를 로컬에서 재현하고, 병목을 하나씩 관찰·해결하는 실험 저장소.
실험 기록은 블로그 시리즈 [MAU 1500만 트래픽 실험](https://gilbert9172.github.io)에 남긴다.

## 목표 수치 (0번 글의 용량 산정)

| 항목 | 값 |
|---|---|
| 설계 목표 RPS | 16,000 (읽기 14,400 / 쓰기 1,600) |
| 피크 DB QPS | 약 9,600 (캐시 히트율 80% 가정) |
| 피크 Redis QPS | 약 38,000 |

## 하드웨어

| 머신 | 역할 |
|---|---|
| MacBook Pro M1 Pro 16GB | 앱 3대 + MySQL + Redis + 관측 (`docker compose up`) |
| MacBook Air M1 8GB | 부하 생성기. `k6 run -e BASE_URL=http://<M1 Pro IP>:8080 ...` |

M1 Pro 단일 머신의 예상 상한은 5,000~8,000 RPS. 16,000 RPS 도달과 수평 확장(가설 10)은 클라우드 단계에서 한다.

## 구성

- `src/` Java 21, Spring Boot 3.4. API 5개: 곡 목록, 곡 상세, 인기 차트, 재생 기록, 좋아요
- `docker-compose.yml` 앱 3대 + nginx + MySQL 8.4 + Redis 7 + Prometheus + Grafana + exporter
- `docker/mysql/init/` 스키마와 시드(아티스트 1만, 곡 100만)
- `k6/` 부하 스크립트. `ramp.js`(베이스라인), `mixed.js`(트래픽 믹스), `single.js`(단일 API 집중)
- `docs/` 실험별 원본 측정 기록

## 실행

```bash
make up          # bootJar → docker compose up
make seed-check  # 시드 완료 확인 (첫 기동 후 1~2분)
make k6-ramp     # 가설 1 베이스라인
make k6-mixed RPS=2000 DURATION=3m
```

- Grafana: http://localhost:3000
- Prometheus: http://localhost:9090
- 앱(nginx 경유): http://localhost:8080

## 실험 스위치

환경변수로 실험 조건을 바꾼다. `docker compose up -d` 전에 export 하거나 `.env`에 둔다.

| 변수 | 기본 | 실험 |
|---|---|---|
| `DB_POOL_SIZE` | 20 | 가설 3: 10 |
| `CHART_TTL_SECONDS` | 60 | 가설 2 |
| `SERVER_SHUTDOWN` | immediate | 가설 9: graceful |
| `JAVA_OPTS` | -Xmx512m G1 | 가설 7 |
| k6 `N_PLUS_ONE=true` | false | 가설 4 |
| k6 `HOT_SONG`, `HOT_RATIO` | 1, 0.5 | 가설 5 |

## 가설 목록

| # | 가설 | 스크립트 |
|---|---|---|
| 1 | 베이스라인: 단일 머신 측정 왜곡 지점 | `ramp.js` |
| 2 | 캐시 스탬피드 | `single.js API=chart` |
| 3 | 커넥션 풀 고갈 | `mixed.js` + `DB_POOL_SIZE=10` + 슬로우 쿼리 주입 |
| 4 | N+1 | `single.js API=list N_PLUS_ONE=true` |
| 5 | 핫 로우 락 경합 | `single.js API=like HOT_RATIO=0.5` |
| 6 | 쓰기 병목 | `single.js API=play TARGET_RPS=1600` |
| 7 | GC와 p99 | `mixed.js` + `JAVA_OPTS` |
| 8 | 인스턴스 장애 | `mixed.js` + `docker compose kill app2` |
| 9 | 무중단 배포 | `mixed.js` + `SERVER_SHUTDOWN` 비교 |
| 10 | 수평 확장 (클라우드) | 앱 2→4→8대, `mixed.js` |
