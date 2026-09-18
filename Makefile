.PHONY: build up down logs seed-check k6-ramp k6-mixed

build:
	./gradlew bootJar -x test

up: build
	docker compose up -d --build

down:
	docker compose down -v

logs:
	docker compose logs -f app1 app2 app3

seed-check:
	docker compose exec mysql mysql -uloadtest -ploadtest loadtest -e "select count(*) songs from song; select count(*) artists from artist;"

# 실험 결과는 k6/results/ 에 남긴다 (gitignore)
k6-ramp:
	mkdir -p k6/results && k6 run --summary-export=k6/results/ramp-$$(date +%Y%m%d-%H%M).json k6/ramp.js

k6-mixed:
	mkdir -p k6/results && k6 run -e TARGET_RPS=$(RPS) -e DURATION=$(DURATION) --summary-export=k6/results/mixed-$(RPS)-$$(date +%Y%m%d-%H%M).json k6/mixed.js
