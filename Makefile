build:
	mvn compile

unit-test:
	@echo "===== Running Unit Tests ====="
	mvn test -P unit-test

integration-test:
	@echo "===== Running Integration Tests ====="
	mvn test -P integration-test

system-test:
	@echo "===== Running System Tests ====="
	make start-app
	mvn test -P system-test
	make stop-app

package:
	@echo "===== Packaging App ====="
	mvn package

start-app:
	@echo "===== Starting App ====="
	docker compose up -d

stop-app:
	@echo "===== Stop App ====="
	docker compose down
