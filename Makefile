artifact_name       := company-exemptions-delta-consumer
version             := "unversioned"

dependency_check_runner := 416670754337.dkr.ecr.eu-west-2.amazonaws.com/dependency-check-runner

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f ./$(artifact_name)-*.zip
	rm -f ./$(artifact_name).jar
	rm -rf ./build-*
	rm -rf ./build.log-*

.PHONY: build
build:
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -Dmaven.test.skip=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit test-integration

.PHONY: test-unit
test-unit:
	mvn test

.PHONY: test-integration
test-integration: clean
	mvn integration-test -Dskip.unit.tests=true failsafe:verify

.PHONY: coverage
coverage:
	mvn verify

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -Dmaven.test.skip=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./start.sh $(tmpdir)
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: build-container
build-container: build
	docker build .

.PHONY: dist
dist: clean build package coverage

.PHONY: publish
publish:
	mvn jar:jar deploy:deploy

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis

.PHONY: dependency-check
dependency-check:
	docker run --rm -e DEPENDENCY_CHECK_SUPPRESSIONS_HOME=/opt -v "$$(pwd)":/app -w /app ${dependency_check_runner} --repo-name="$(basename "$$(pwd)")"
.PHONY: security-check
security-check: dependency-check
