<?php

require 'vendor/autoload.php';

use Dotenv\Dotenv;
use LogBull\Core\LogBullLogger;
use LogBull\Core\Types;
use LogBull\Handlers\MonologHandler;
use LogBull\Handlers\PSR3Logger;
use Monolog\Logger;
use Monolog\Level;

// Load environment variables from parent directory (testing/.env)
$dotenv = Dotenv::createImmutable(__DIR__ . '/..');
$dotenv->load();

// Configuration from environment variables
$projectId = $_ENV['LOGBULL_PROJECT_ID'] ?? null;
$host = $_ENV['LOGBULL_HOST'] ?? null;
$apiKey = $_ENV['LOGBULL_API_KEY'] ?? null;

// Convert empty string to null for API key
if ($apiKey === '' || strlen($apiKey) < 10) {
    $apiKey = null;
}

// Validate required variables
if (!$projectId) {
    die("LOGBULL_PROJECT_ID environment variable is required\n");
}
if (!$host) {
    die("LOGBULL_HOST environment variable is required\n");
}

function demoLogBullLogger(string $projectId, string $host, ?string $apiKey): void
{
    echo "\n=== LogBull Logger Demo ===\n";

    $logger = new LogBullLogger(
        projectId: $projectId,
        host: $host,
        apiKey: $apiKey,
        logLevel: Types::DEBUG
    );

    // Basic logging
    $logger->info('[php - logbull_logger - 1] User logged in successfully', [
        'user_id' => '12345',
        'username' => 'john_doe',
        'ip' => '192.168.1.100'
    ]);

    $logger->error('[php - logbull_logger - 2] Database connection failed', [
        'database' => 'users_db',
        'error_code' => 500
    ]);

    $logger->debug('[php - logbull_logger - 3] Processing user data', [
        'step' => 'validation',
        'user_id' => '12345'
    ]);

    // Context management
    $sessionLogger = $logger->withContext([
        'session_id' => 'sess_abc123',
        'user_id' => 'user_456',
        'request_id' => 'req_789'
    ]);

    $sessionLogger->info('[php - logbull_logger - 4] User started checkout process', [
        'cart_items' => 3,
        'total_amount' => 149.99
    ]);

    $sessionLogger->warning('[php - logbull_logger - 5] Payment processing slow', [
        'payment_method' => 'credit_card',
        'processing_time_ms' => 5000
    ]);

    $logger->flush();
    echo "LogBull Logger demo completed\n";
}

function demoMonologLogger(string $projectId, string $host, ?string $apiKey): void
{
    echo "\n=== Monolog Logger Demo ===\n";

    $handler = new MonologHandler(
        projectId: $projectId,
        host: $host,
        apiKey: $apiKey,
        level: Level::Info
    );

    $logger = new Logger('app');
    $logger->pushHandler($handler);

    $logger->info('[php - monolog_logger - 1] Application started', [
        'version' => '1.0.0',
        'environment' => 'production'
    ]);

    $logger->warning('[php - monolog_logger - 2] Rate limit approaching', [
        'current_requests' => 950,
        'limit' => 1000,
        'user_id' => 'user_789'
    ]);

    $logger->error('[php - monolog_logger - 3] Database error', [
        'query' => 'SELECT * FROM users',
        'error' => 'Connection timeout',
        'retry_count' => 3
    ]);

    $logger->info('[php - monolog_logger - 4] User action', [
        'user_id' => '12345',
        'action' => 'login',
        'ip' => '192.168.1.100'
    ]);

    $logger->error('[php - monolog_logger - 5] Payment failed', [
        'order_id' => 'ord_123',
        'amount' => 99.99,
        'currency' => 'USD'
    ]);

    $handler->flush();
    echo "Monolog Logger demo completed\n";
}

function demoPSR3Logger(string $projectId, string $host, ?string $apiKey): void
{
    echo "\n=== PSR-3 Logger Demo ===\n";

    $logger = new PSR3Logger(
        projectId: $projectId,
        host: $host,
        apiKey: $apiKey,
        logLevel: Types::INFO
    );

    $logger->info('[php - psr3_logger - 1] API request', [
        'method' => 'POST',
        'path' => '/api/users',
        'status_code' => 201,
        'response_time_ms' => 45
    ]);

    $logger->warning('[php - psr3_logger - 2] Cache miss', [
        'cache_key' => 'user_profile_12345',
        'fallback' => 'database_query'
    ]);

    $logger->error('[php - psr3_logger - 3] Database error', [
        'query' => 'SELECT * FROM users',
        'error' => 'Connection timeout'
    ]);

    $logger->info('[php - psr3_logger - 4] Request started', [
        'request_id' => 'req_789',
        'session_id' => 'sess_456',
        'endpoint' => '/api/users'
    ]);

    $logger->critical('[php - psr3_logger - 5] System overload detected', [
        'cpu_usage' => 95.5,
        'memory_usage' => 87.2,
        'active_connections' => 1500
    ]);

    $logger->flush();
    echo "PSR-3 Logger demo completed\n";
}

function main(): void
{
    global $projectId, $host, $apiKey;

    echo "LogBull PHP Library Demo\n";
    echo str_repeat('=', 40) . "\n";

    try {
        // Demonstrate each logger type
        demoLogBullLogger($projectId, $host, $apiKey);
        demoMonologLogger($projectId, $host, $apiKey);
        demoPSR3Logger($projectId, $host, $apiKey);

        echo "\n=== Demo Complete ===\n";
        echo "Check your LogBull server for the logged messages!\n";

        // Wait for logs to be sent
        for ($i = 1; $i <= 5; $i++) {
            echo "Waiting for {$i} seconds...\n";
            sleep(1);
        }
    } catch (Exception $e) {
        echo "Error during demo: {$e->getMessage()}\n";
        echo "Make sure your LogBull server is running and configuration is correct.\n";
        exit(1);
    }
}

main();

