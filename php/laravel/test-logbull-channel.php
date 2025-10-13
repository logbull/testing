<?php

require __DIR__ . '/vendor/autoload.php';

use Illuminate\Container\Container;
use Illuminate\Support\Facades\Facade;

// Bootstrap Laravel application
$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

Facade::setFacadeApplication($app);

echo "Testing LogBull channel creation...\n\n";

try {
    echo "1. Checking config...\n";
    $config = config('logging.channels.logbull');
    echo "Config: " . print_r($config, true) . "\n";
    
    echo "\n2. Creating channel...\n";
    $logger = Log::channel('logbull');
    
    echo "\n3. Getting Monolog logger...\n";
    $monologLogger = $logger->getLogger();
    
    echo "\n4. Getting handlers...\n";
    $handlers = $monologLogger->getHandlers();
    
    echo "Number of handlers: " . count($handlers) . "\n";
    foreach ($handlers as $index => $handler) {
        echo "Handler {$index}: " . get_class($handler) . "\n";
    }
    
    echo "\n5. Testing a log message...\n";
    $logger->info('Test message from debug script');
    
    echo "\n✓ Success!\n";
    
} catch (\Exception $e) {
    echo "\n✗ Error: " . $e->getMessage() . "\n";
    echo "Stack trace:\n" . $e->getTraceAsString() . "\n";
}


