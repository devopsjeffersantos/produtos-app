MVN_REPORT := target/site/surefire-report.html
TIMESTAMP := $(shell date +'%F %T')

MVN_ARGS := -Dorg.slf4j.simpleLogger.showDateTime=true \
			-Dorg.slf4j.simpleLogger.dateTimeFormat="yyyy-MM-dd HH:mm:ss.SSS" \
			-Dorg.slf4j.simpleLogger.showLogName=false \
			-Dorg.slf4j.simpleLogger.showShortLogName=false \
			-Dorg.slf4j.simpleLogger.showThreadName=false

compile: # compile project
	@./mvnw $(MVN_ARGS) clean compile test-compile

package:
	@./mvnw $(MVN_ARGS) package -DskipTests

start-api:
	@./mvnw $(MVN_ARGS) clean spring-boot:run

start-jar: package
	@java -jar ./target/produtos-app-*.jar

start-docker:
	@docker run --rm --name produtos-app -p 8080:80 -it -e DATASOURCE_URL=${DATASOURCE_URL} -e DATASOURCE_USERNAME=${DATASOURCE_USERNAME} -e DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD} produtos-app:latest

debug-api:
	@./mvnw $(MVN_ARGS) clean spring-boot:run -Dspring-boot.run.profiles=dev -Dspring.jmx.enabled=true

## Test

unit-test:
	@./mvnw $(MVN_ARGS) test

integration-test:
	@./mvnw $(MVN_ARGS) test -P integration-test
	@./mvnw jacoco:report

system-test:
	@./mvnw $(MVN_ARGS) test -Psystem-test
	@echo $(TIMESTAMP) [INFO] cucumber HTML report generate in: target/cucumber-reports/cucumber.html

performance-test:
	@./mvnw clean verify -Pperformance-test

test: unit-test integration-test performance-test


report-maven: # Gerar relatorio HTML utilizando maven
	@./mvnw $(MVN_ARGS) surefire-report:report
	@echo $(TIMESTAMP) [INFO] maven report generate in: $(MVN_REPORT)

report-allure:
	@./mvnw clean verify
	allure serve ./target/allure-results 

## Docker

docker-image: package
	docker build -t produtos-app:latest -f ./Dockerfile .
