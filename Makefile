build:
	mvn compile

unit-test:
	mvn test -P unit-test

integration-test:
	mvn test -P integration-test

system-test:
	make start-app
	mvn test -P system-test
	make stop-app

package:
	mvn package

start-app:
	docker compose up -d

stop-app:
	docker compose down