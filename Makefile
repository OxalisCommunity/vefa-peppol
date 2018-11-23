.PHONY: build release build-docker
HOME := $(if $(HOME),$(HOME),$(shell echo $HOME))
M2 := $(if $(M2),$(M2),$(HOME)/.m2)

build:
	@mvn clean install

build-docker:
	@docker run --rm -it \
		-v "$(shell pwd)":/src \
		-v $(M2):/root/.m2 \
		-w /src \
		maven:3.3-jdk-8 \
		mvn clean install

release:
	@mvn clean release:prepare release:perform
