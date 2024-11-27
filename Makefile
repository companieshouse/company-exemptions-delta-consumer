artifact_name       := company-exemptions-delta-consumer
version             := latest

.PHONY: all
all: build

.PHONY: clean
clean:
	mvn clean
	rm -f $(artifact_name)-*.zip
	rm -f $(artifact_name).jar
	rm -rf ./build-*
	rm -f ./build.log

.PHONY: build
build:
	# Temporary workaround for failure on concourse - waiting for artifactory request of new version to be actioned by platform
	mvn org.codehaus.mojo:versions-maven-plugin:2.16.2:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -Dskip.unit.tests=true
	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

	# Original make build command below
#	mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
#	mvn package -Dskip.unit.tests=true
#	cp ./target/$(artifact_name)-$(version).jar ./$(artifact_name).jar

.PHONY: test
test: test-unit test-integration

.PHONY: test-unit
test-unit:
	mvn clean verify

.PHONY: test-integration
test-integration:
	mvn clean verify -Dskip.unit.tests=true -Dskip.integration.tests=false

.PHONY: package
package:
ifndef version
	$(error No version given. Aborting)
endif
	$(info Packaging version: $(version))
	# Temporary workaround for failure on concourse - waiting for artifactory request of new version to be actioned by platform
	mvn org.codehaus.mojo:versions-maven-plugin:2.17.1:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	#mvn versions:set -DnewVersion=$(version) -DgenerateBackupPoms=false
	mvn package -Dskip.unit.tests=true
	$(eval tmpdir:=$(shell mktemp -d build-XXXXXXXXXX))
	cp ./start.sh $(tmpdir)
	cp ./target/$(artifact_name)-$(version).jar $(tmpdir)/$(artifact_name).jar
	cd $(tmpdir); zip -r ../$(artifact_name)-$(version).zip *
	rm -rf $(tmpdir)

.PHONY: dist
dist: clean build package

.PHONY: sonar
sonar:
	mvn sonar:sonar

.PHONY: sonar-pr-analysis
sonar-pr-analysis:
	mvn sonar:sonar -P sonar-pr-analysis
