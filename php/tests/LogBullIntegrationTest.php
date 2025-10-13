<?php

namespace LogBullTesting\Tests;

use LogBull\Core\LogBullLogger;
use LogBull\Core\Types;
use LogBull\Handlers\MonologHandler;
use LogBull\Handlers\PSR3Logger;
use Monolog\Logger;
use Monolog\Level;
use PHPUnit\Framework\TestCase;

class LogBullIntegrationTest extends TestCase
{
    private string $projectId;
    private string $host;
    private string $apiKey;

    protected function setUp(): void
    {
        parent::setUp();
        
        // Use test credentials
        $this->projectId = '00000000-0000-0000-0000-000000000000';
        $this->host = 'http://localhost:4005';
        $this->apiKey = 'test-api-key';
    }

    public function test_logbull_logger_basic_logging(): void
    {
        $logger = new LogBullLogger(
            projectId: $this->projectId,
            host: $this->host,
            apiKey: $this->apiKey,
            logLevel: Types::DEBUG
        );

        // Test basic logging without exceptions
        $logger->info('Test info message', ['key' => 'value']);
        $logger->error('Test error message', ['error_code' => 500]);
        $logger->debug('Test debug message');
        
        $logger->flush();
        
        $this->assertTrue(true);
    }

    public function test_logbull_logger_context_management(): void
    {
        $logger = new LogBullLogger(
            projectId: $this->projectId,
            host: $this->host,
            apiKey: $this->apiKey
        );

        $contextLogger = $logger->withContext([
            'session_id' => 'test_session',
            'user_id' => 'test_user'
        ]);

        $contextLogger->info('Message with context', ['action' => 'test']);
        $logger->flush();
        
        $this->assertTrue(true);
    }

    public function test_monolog_handler_integration(): void
    {
        $handler = new MonologHandler(
            projectId: $this->projectId,
            host: $this->host,
            apiKey: $this->apiKey,
            level: Level::Info
        );

        $logger = new Logger('test');
        $logger->pushHandler($handler);

        $logger->info('Test monolog message', ['test' => true]);
        $logger->error('Test monolog error', ['error' => 'test']);
        
        $handler->flush();
        
        $this->assertTrue(true);
    }

    public function test_psr3_logger_implementation(): void
    {
        $logger = new PSR3Logger(
            projectId: $this->projectId,
            host: $this->host,
            apiKey: $this->apiKey,
            logLevel: Types::INFO
        );

        $logger->info('PSR-3 test message', ['psr3' => true]);
        $logger->warning('PSR-3 warning', ['level' => 'warning']);
        $logger->error('PSR-3 error', ['level' => 'error']);
        
        $logger->flush();
        
        $this->assertTrue(true);
    }

    public function test_multiple_log_levels(): void
    {
        $logger = new LogBullLogger(
            projectId: $this->projectId,
            host: $this->host,
            apiKey: $this->apiKey,
            logLevel: Types::DEBUG
        );

        $logger->debug('Debug level');
        $logger->info('Info level');
        $logger->warning('Warning level');
        $logger->error('Error level');
        $logger->critical('Critical level');
        
        $logger->flush();
        
        $this->assertTrue(true);
    }

    public function test_consecutive_logging_operations(): void
    {
        $logger = new LogBullLogger(
            projectId: $this->projectId,
            host: $this->host,
            apiKey: $this->apiKey
        );

        for ($i = 1; $i <= 10; $i++) {
            $logger->info("Log message #{$i}", ['iteration' => $i]);
        }
        
        $logger->flush();
        
        $this->assertTrue(true);
    }
}

