<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use Illuminate\Support\Facades\Log;

class TestLogBullCommand extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'logbull:test';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Test LogBull Laravel integration by sending sample logs';

    /**
     * Execute the console command.
     */
    public function handle(): int
    {
        $this->info('LogBull Laravel Integration Test');
        $this->info(str_repeat('=', 40));

        try {
            // Basic logging through LogBull channel
            Log::channel('logbull')->info('[php - laravel - 1] Application test started', [
                'version' => '1.0.0',
                'environment' => config('app.env')
            ]);

            Log::channel('logbull')->info('[php - laravel - 2] User action recorded', [
                'user_id' => '12345',
                'action' => 'login',
                'ip' => '192.168.1.100'
            ]);

            Log::channel('logbull')->warning('[php - laravel - 3] Rate limit approaching', [
                'current_requests' => 950,
                'limit' => 1000,
                'user_id' => 'user_789'
            ]);

            Log::channel('logbull')->error('[php - laravel - 4] Payment processing failed', [
                'order_id' => 'ord_123',
                'amount' => 99.99,
                'currency' => 'USD',
                'error_reason' => 'insufficient_funds'
            ]);

            Log::channel('logbull')->info('[php - laravel - 5] Request completed', [
                'request_id' => 'req_789',
                'duration_ms' => 250,
                'status_code' => 200
            ]);

            $this->info('All logs sent successfully!');
            
            // Flush the LogBull channel to ensure logs are sent
            $logger = Log::channel('logbull');
            $monologLogger = $logger->getLogger();
            
            foreach ($monologLogger->getHandlers() as $handler) {
                if (method_exists($handler, 'flush')) {
                    $handler->flush();
                }
            }
            
            $this->info('Waiting for logs to be processed...');

            // Wait for logs to be sent
            for ($i = 1; $i <= 5; $i++) {
                $this->info("Waiting {$i}/5 seconds...");
                sleep(1);
            }

            $this->info('Test completed successfully!');
            return Command::SUCCESS;
        } catch (\Exception $e) {
            $this->error("Error during test: {$e->getMessage()}");
            return Command::FAILURE;
        }
    }
}

