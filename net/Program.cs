using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using DotNetEnv;
using LogBull;
using LogBull.Core;
using LogBull.Extensions;
using LogBull.Serilog;
using Microsoft.Extensions.Logging;
using Serilog;
using Serilog.Context;

namespace LogBullTest;

class Program
{
    private static string? _projectId;
    private static string? _host;
    private static string? _apiKey;

    static async Task Main(string[] args)
    {
        Console.WriteLine("LogBull .NET Library Integration Tests");
        Console.WriteLine("========================================");

        try
        {
            // Load configuration
            LoadConfiguration();

            // Validate configuration
            ValidateConfiguration();

            // Run all demos
            DemoLogBullLogger();
            DemoMicrosoftExtensionsLogging();
            DemoSerilogIntegration();

            Console.WriteLine("\n=== Demo Complete ===");
            Console.WriteLine("Check your LogBull server for the logged messages!");

            // Wait for logs to be sent
            for (int i = 1; i <= 5; i++)
            {
                Console.WriteLine($"Waiting for {i} seconds...");
                await Task.Delay(1000);
            }
        }
        catch (Exception ex)
        {
            Console.Error.WriteLine($"Error during demo: {ex.Message}");
            Console.Error.WriteLine("Make sure your LogBull server is running and configuration is correct.");
            Environment.Exit(1);
        }
    }

    static void LoadConfiguration()
    {
        // Try to load .env file from parent directory (testing/.env)
        var envPath = Path.Combine("..", ".env");
        if (File.Exists(envPath))
        {
            Env.Load(envPath);
            Console.WriteLine($"Loaded configuration from {envPath}");
        }
        else
        {
            Console.WriteLine("Warning: ../.env file not found, using environment variables");
        }

        // Load environment variables
        _projectId = Environment.GetEnvironmentVariable("LOGBULL_PROJECT_ID");
        _host = Environment.GetEnvironmentVariable("LOGBULL_HOST");
        _apiKey = Environment.GetEnvironmentVariable("LOGBULL_API_KEY");
    }

    static void ValidateConfiguration()
    {
        if (string.IsNullOrWhiteSpace(_projectId))
        {
            throw new InvalidOperationException("LOGBULL_PROJECT_ID environment variable is required");
        }

        if (string.IsNullOrWhiteSpace(_host))
        {
            throw new InvalidOperationException("LOGBULL_HOST environment variable is required");
        }

        Console.WriteLine($"Configuration loaded:");
        Console.WriteLine($"  Project ID: {_projectId}");
        Console.WriteLine($"  Host: {_host}");
        Console.WriteLine($"  API Key: {(_apiKey != null && _apiKey.Length > 0 ? "***" : "(not set)")}");
    }

    static void DemoLogBullLogger()
    {
        Console.WriteLine("\n=== LogBull Logger Demo ===");

        var logger = LogBullLogger.CreateBuilder()
            .WithProjectId(_projectId!)
            .WithHost(_host!)
            .WithApiKey(_apiKey)
            .WithLogLevel(LogBull.Core.LogLevel.DEBUG)
            .Build();

        // Basic logging with different levels
        logger.Debug("[net - logbull_logger - 1] Processing user data", new Dictionary<string, object>
        {
            { "step", "validation" },
            { "user_id", "12345" }
        });

        logger.Info("[net - logbull_logger - 2] User logged in successfully", new Dictionary<string, object>
        {
            { "user_id", "12345" },
            { "username", "john_doe" },
            { "ip", "192.168.1.100" }
        });

        logger.Warning("[net - logbull_logger - 3] Payment processing slow", new Dictionary<string, object>
        {
            { "payment_method", "credit_card" },
            { "processing_time_ms", 5000 }
        });

        logger.Error("[net - logbull_logger - 4] Database connection failed", new Dictionary<string, object>
        {
            { "database", "users_db" },
            { "error_code", 500 }
        });

        logger.Critical("[net - logbull_logger - 5] System overload detected", new Dictionary<string, object>
        {
            { "cpu_usage", 95.5 },
            { "memory_usage", 87.2 },
            { "active_connections", 1500 }
        });

        // Context management
        var sessionLogger = logger.WithContext(new Dictionary<string, object>
        {
            { "session_id", "sess_abc123" },
            { "user_id", "user_456" },
            { "request_id", "req_789" }
        });

        sessionLogger.Info("[net - logbull_logger - 6] User started checkout process", new Dictionary<string, object>
        {
            { "cart_items", 3 },
            { "total_amount", 149.99 }
        });

        // Nested context
        var transactionLogger = sessionLogger.WithContext(new Dictionary<string, object>
        {
            { "transaction_id", "txn_xyz789" },
            { "merchant_id", "merchant_123" }
        });

        transactionLogger.Info("[net - logbull_logger - 7] Transaction completed", new Dictionary<string, object>
        {
            { "amount", 149.99 },
            { "currency", "USD" },
            { "status", "completed" }
        });

        logger.Flush();
        logger.Dispose();

        Console.WriteLine("LogBull Logger demo completed");
    }

    static void DemoMicrosoftExtensionsLogging()
    {
        Console.WriteLine("\n=== Microsoft.Extensions.Logging Demo ===");

        var loggerFactory = LoggerFactory.Create(builder =>
        {
            builder.AddLogBull(configBuilder =>
            {
                configBuilder
                    .WithProjectId(_projectId!)
                    .WithHost(_host!)
                    .WithApiKey(_apiKey)
                    .WithLogLevel(LogBull.Core.LogLevel.INFO);
            });
        });

        var logger = loggerFactory.CreateLogger<Program>();

        // Structured logging with placeholders
        logger.LogInformation("[net - mel - 1] Application started with version {Version} in {Environment}",
            "1.0.0", "production");

        logger.LogWarning("[net - mel - 2] Rate limit approaching: {CurrentRequests}/{Limit} for user {UserId}",
            950, 1000, "user_789");

        logger.LogError("[net - mel - 3] Database error with query {Query}: {Error}, retry {RetryCount}",
            "SELECT * FROM users", "Connection timeout", 3);

        logger.LogInformation("[net - mel - 4] User action: {UserId} performed {Action} from {IpAddress}",
            "12345", "login", "192.168.1.100");

        logger.LogError("[net - mel - 5] Payment failed for order {OrderId} with amount {Amount} {Currency}",
            "ord_123", 99.99, "USD");

        // Using scopes for context
        using (logger.BeginScope(new Dictionary<string, object>
        {
            { "request_id", "req_789" },
            { "session_id", "sess_456" }
        }))
        {
            logger.LogInformation("[net - mel - 6] Request started at endpoint {Endpoint}", "/api/users");

            logger.LogInformation("[net - mel - 7] Request completed with status {StatusCode} in {DurationMs}ms",
                200, 250);
        }

        logger.LogCritical("[net - mel - 8] System resources critical: CPU={CpuUsage}%, Memory={MemoryUsage}%, Connections={ActiveConnections}",
            95.5, 87.2, 1500);

        loggerFactory.Dispose();

        Console.WriteLine("Microsoft.Extensions.Logging demo completed");
    }

    static void DemoSerilogIntegration()
    {
        Console.WriteLine("\n=== Serilog Integration Demo ===");

        var config = Config.CreateBuilder()
            .WithProjectId(_projectId!)
            .WithHost(_host!)
            .WithApiKey(_apiKey)
            .WithLogLevel(LogBull.Core.LogLevel.INFO)
            .Build();

        var sink = new LogBullSink(config);

        Log.Logger = new LoggerConfiguration()
            .MinimumLevel.Debug()
            .WriteTo.Sink(sink)
            .CreateLogger();

        // Structured logging with properties
        Log.Information("[net - serilog - 1] API request {Method} {Path} returned {StatusCode} in {ResponseTimeMs}ms",
            "POST", "/api/users", 201, 45);

        Log.Warning("[net - serilog - 2] Cache miss for key {CacheKey}, using fallback {Fallback}",
            "user_profile_12345", "database_query");

        Log.Error("[net - serilog - 3] Database error: {Query} failed with {Error}",
            "SELECT * FROM users", "Connection timeout");

        // Using context properties (enrichment)
        using (LogContext.PushProperty("CorrelationId", "corr_123"))
        using (LogContext.PushProperty("UserId", "user_789"))
        {
            Log.Information("[net - serilog - 4] Processing payment: {Amount} {Currency} via {PaymentGateway}",
                150.00, "EUR", "stripe");

            Log.Error("[net - serilog - 5] Payment gateway error: {ErrorCode}, retry {RetryCount}/{MaxRetries}",
                "GATEWAY_TIMEOUT", 3, 5);
        }

        // Structured object logging
        var requestInfo = new
        {
            RequestId = "req_789",
            SessionId = "sess_456",
            Endpoint = "/api/users",
            Method = "POST",
            UserAgent = "Mozilla/5.0"
        };

        Log.Information("[net - serilog - 6] Request details: {@RequestInfo}", requestInfo);

        Log.Fatal("[net - serilog - 7] Critical system failure: CPU={CpuUsage}, Memory={MemoryUsage}, Connections={ActiveConnections}",
            95.5, 87.2, 1500);

        // Test exception logging
        try
        {
            throw new InvalidOperationException("Test exception for logging");
        }
        catch (Exception ex)
        {
            Log.Error(ex, "[net - serilog - 8] Operation failed with exception");
        }

        sink.Flush();
        
        // Wait for logs to be sent
        System.Threading.Thread.Sleep(2000);
        
        Log.CloseAndFlush();
        sink.Dispose();

        Console.WriteLine("Serilog Integration demo completed");
    }
}

