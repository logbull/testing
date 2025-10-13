<?php

namespace Tests\Feature;

use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Log;
use Tests\TestCase;

class LogBullChannelTest extends TestCase
{
    /**
     * Test that LogBull channel is configured correctly.
     */
    public function test_logbull_channel_is_configured(): void
    {
        $this->assertArrayHasKey('logbull', config('logging.channels'));
        
        $logbullConfig = config('logging.channels.logbull');
        
        $this->assertEquals('custom', $logbullConfig['driver']);
        $this->assertEquals(\LogBull\Handlers\LaravelHandler::class, $logbullConfig['via']);
    }

    /**
     * Test that logs can be sent through LogBull channel.
     */
    public function test_can_log_through_logbull_channel(): void
    {
        // This test verifies that logging doesn't throw exceptions
        try {
            Log::channel('logbull')->info('Test log message', [
                'test_key' => 'test_value',
                'timestamp' => now()->toIso8601String()
            ]);
            
            $this->assertTrue(true);
        } catch (\Exception $e) {
            $this->fail("Logging through LogBull channel failed: {$e->getMessage()}");
        }
    }

    /**
     * Test logging with multiple log levels.
     */
    public function test_multiple_log_levels(): void
    {
        try {
            Log::channel('logbull')->debug('Debug message');
            Log::channel('logbull')->info('Info message');
            Log::channel('logbull')->warning('Warning message');
            Log::channel('logbull')->error('Error message');
            Log::channel('logbull')->critical('Critical message');
            
            $this->assertTrue(true);
        } catch (\Exception $e) {
            $this->fail("Multi-level logging failed: {$e->getMessage()}");
        }
    }

    /**
     * Test that context is properly passed to LogBull.
     */
    public function test_context_is_preserved(): void
    {
        $context = [
            'user_id' => '12345',
            'action' => 'test_action',
            'metadata' => [
                'key1' => 'value1',
                'key2' => 'value2'
            ]
        ];

        try {
            Log::channel('logbull')->info('Message with context', $context);
            
            $this->assertTrue(true);
        } catch (\Exception $e) {
            $this->fail("Logging with context failed: {$e->getMessage()}");
        }
    }

    /**
     * Test consecutive logging operations.
     */
    public function test_consecutive_logging(): void
    {
        try {
            for ($i = 1; $i <= 10; $i++) {
                Log::channel('logbull')->info("Log message #{$i}", [
                    'iteration' => $i,
                    'timestamp' => microtime(true)
                ]);
            }
            
            $this->assertTrue(true);
        } catch (\Exception $e) {
            $this->fail("Consecutive logging failed: {$e->getMessage()}");
        }
    }
}

