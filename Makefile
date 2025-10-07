install:
	cd go && go mod download
	cd python && uv sync
	cd js && npm install

test:
	cd go && go run main.go
	cd python && uv run main.py
	cd js && npm start
	cd java && ./gradlew test

test-windows:
	cd go && go run main.go
	cd python && uv run main.py
	cd js && npm start
	cd java && gradlew.bat test
	
