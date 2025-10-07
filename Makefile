install:
	cd go && go mod download
	cd python && uv sync

test:
	cd go && go run main.go
	cd python && uv run main.py
	cd java && ./gradlew test

test-windows:
	cd go && go run main.go
	cd python && uv run main.py
	cd java && gradlew.bat test
	
