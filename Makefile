install:
	cd go && go mod download
	cd python && uv sync
	cd js && npm install
	cd php && composer install
	cd php/laravel && composer install --ignore-platform-req=ext-fileinfo

test:
	cd go && go run main.go
	cd python && uv run main.py
	cd js && npm start
	cd java && ./gradlew test
	cd php && php main.php
	cd php/laravel && php artisan logbull:test

test-windows:
	cd go && go run main.go
	cd python && uv run main.py
	cd js && npm start
	cd java && gradlew.bat test
	cd php && php main.php
	cd php/laravel && php artisan logbull:test
	
