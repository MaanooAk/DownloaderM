
.phony: default
default:
	mvn package exec:java

.phony: help
help:
	@echo "Usage: make [option]"
