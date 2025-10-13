# LogBull PHP Testing

This directory contains testing scripts for the LogBull PHP library.

## Structure

```
php/
├── main.php                    # Main testing script for standalone loggers
├── tests/                      # PHPUnit tests for standalone loggers
│   └── LogBullIntegrationTest.php
├── laravel/                    # Laravel integration testing
│   ├── app/
│   │   └── Console/Commands/
│   │       └── TestLogBullCommand.php
│   ├── config/
│   │   ├── app.php
│   │   └── logging.php
│   └── tests/
│       └── Feature/
│           └── LogBullChannelTest.php
├── composer.json
└── phpunit.xml
```

## Setup

### 1. Environment Configuration

Copy the parent directory's `.env.example` to `.env` and fill in your LogBull credentials:

```bash
cd ..
cp .env.example .env
# Edit .env with your values
```

### 2. Install Dependencies

```bash
# Install PHP dependencies
composer install

# Install Laravel dependencies
cd laravel
composer install
cd ..
```

### 3. Laravel Configuration

Copy Laravel's `.env.example` to `.env`:

```bash
cd laravel
cp .env.example .env
# Edit .env with your LogBull credentials
cd ..
```

## Running Tests

### Run All Tests

From the testing directory root:

```bash
make install  # Install all dependencies
make test     # Run all tests
```

### Run PHP Tests Only

```bash
# Run standalone logger demos
php main.php

# Run PHPUnit tests for standalone loggers
composer test

# Run Laravel console command demo
cd laravel && php artisan logbull:test

# Run Laravel PHPUnit tests
cd laravel && composer test
```

## What Gets Tested

### Standalone Loggers (`main.php`)

1. **LogBullLogger**: Basic logging with context management
2. **MonologHandler**: Monolog integration with LogBull
3. **PSR3Logger**: PSR-3 compatible logger implementation

### PHPUnit Tests (`tests/`)

Integration tests covering:

- Basic logging operations
- Context management
- Monolog integration
- PSR-3 implementation
- Multiple log levels
- Consecutive logging operations

### Laravel Integration (`laravel/`)

1. **Console Command** (`php artisan logbull:test`): Demonstrates Laravel channel logging
   - **Important**: Laravel logs require explicit flushing via `$handler->flush()` to ensure logs are sent
2. **PHPUnit Tests**: Verifies Laravel channel configuration and logging

## Requirements

- PHP 8.0 or higher
- Composer
- LogBull server running (for actual log delivery)

## Expected Behavior

All tests should complete without errors. The scripts will:

1. Send log messages to your LogBull server
2. Wait 5 seconds for asynchronous delivery
3. Exit successfully

Check your LogBull dashboard to verify logs were received with proper:

- Log levels (DEBUG, INFO, WARNING, ERROR, CRITICAL)
- Context fields
- Message formatting: `[php - logger_name - N] Message text`

## Laravel Integration Notes

**Important**: When using Laravel's Log channel, logs are queued but need to be flushed to ensure delivery.

### In Commands or Jobs

```php
// In your Laravel command or job
Log::channel('logbull')->info('User logged in', ['user_id' => 123]);

// Flush logs before exit
$logger = Log::channel('logbull')->getLogger();
foreach ($logger->getHandlers() as $handler) {
    if (method_exists($handler, 'flush')) {
        $handler->flush();
    }
}
```

### Application-Wide (Recommended)

Register a shutdown handler in `app/Providers/AppServiceProvider.php`:

```php
public function boot(): void
{
    $this->app->terminating(function () {
        $logger = Log::channel('logbull')->getLogger();
        foreach ($logger->getHandlers() as $handler) {
            if (method_exists($handler, 'flush')) {
                $handler->flush();
            }
        }
    });
}
```

This ensures all queued logs are sent when the application terminates.
