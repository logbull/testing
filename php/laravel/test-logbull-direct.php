<?php

require __DIR__ . '/vendor/autoload.php';

echo "Testing LogBull Handler directly...\n\n";

try {
    echo "1. Creating LaravelHandler instance...\n";
    $handlerClass = new \LogBull\Handlers\LaravelHandler();
    echo "✓ Handler class instantiated\n";
    
    echo "\n2. Calling handler with config...\n";
    $config = [
        'project_id' => '0cb83477-89dd-40ec-b282-b2fd92579fa3',
        'host' => 'http://88.218.122.48:4005',
        'api_key' => '',
        'level' => 'debug',
    ];
    
    $logger = $handlerClass($config);
    echo "✓ Logger created: " . get_class($logger) . "\n";
    
    echo "\n3. Getting handlers...\n";
    $handlers = $logger->getHandlers();
    echo "Number of handlers: " . count($handlers) . "\n";
    foreach ($handlers as $index => $handler) {
        $handlerClass = get_class($handler);
        echo "Handler {$index}: {$handlerClass}\n";
    }
    
    echo "\n4. Testing log message...\n";
    $logger->info('[php - laravel-direct - 1] Direct test message');
    
    echo "\n5. Flushing...\n";
    foreach ($handlers as $handler) {
        if (method_exists($handler, 'flush')) {
            $handler->flush();
            echo "✓ Flushed\n";
        }
    }
    
    echo "\n✓ All tests passed!\n";
    
} catch (\Exception $e) {
    echo "\n✗ Error: " . $e->getMessage() . "\n";
    echo "File: " . $e->getFile() . ":" . $e->getLine() . "\n";
    echo "Stack trace:\n" . $e->getTraceAsString() . "\n";
}


