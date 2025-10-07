package main

import (
	"fmt"
	"log"
	"log/slog"
	"os"
	"time"

	"github.com/joho/godotenv"
	"github.com/logbull/logbull-go/logbull"
	"github.com/sirupsen/logrus"
	"go.uber.org/zap"
)

var (
	projectID string
	host      string
	apiKey    string
)

func init() {
	// Load .env file from parent directory (testing/.env)
	if err := godotenv.Load("../.env"); err != nil {
		log.Println("Warning: ../.env file not found, using environment variables")
	}

	// Load environment variables (with GO_ prefix)
	projectID = os.Getenv("LOGBULL_PROJECT_ID")
	host = os.Getenv("LOGBULL_HOST")
	apiKey = os.Getenv("LOGBULL_API_KEY")

	// Validate required variables
	if projectID == "" {
		log.Fatal("LOGBULL_PROJECT_ID environment variable is required")
	}
	if host == "" {
		log.Fatal("LOGBULL_HOST environment variable is required")
	}
}

func demoLogBullLogger() {
	fmt.Println("\n=== LogBull Logger Demo ===")

	logger, err := logbull.NewLogger(logbull.Config{
		ProjectID: projectID,
		Host:      host,
		APIKey:    apiKey,
		LogLevel:  logbull.DEBUG,
	})
	if err != nil {
		panic(err)
	}
	defer logger.Shutdown()

	// Basic logging
	logger.Info("[go - logbull_logger - 1] User logged in successfully", map[string]any{
		"user_id":  "12345",
		"username": "john_doe",
		"ip":       "192.168.1.100",
	})

	logger.Error("[go - logbull_logger - 2] Database connection failed", map[string]any{
		"database":   "users_db",
		"error_code": 500,
	})

	logger.Debug("[go - logbull_logger - 3] Processing user data", map[string]any{
		"step":    "validation",
		"user_id": "12345",
	})

	// Context management
	sessionLogger := logger.WithContext(map[string]any{
		"session_id": "sess_abc123",
		"user_id":    "user_456",
		"request_id": "req_789",
	})

	sessionLogger.Info("[go - logbull_logger - 4] User started checkout process", map[string]any{
		"cart_items":   3,
		"total_amount": 149.99,
	})

	sessionLogger.Warning("[go - logbull_logger - 5] Payment processing slow", map[string]any{
		"payment_method":     "credit_card",
		"processing_time_ms": 5000,
	})

	logger.Flush()
	fmt.Println("LogBull Logger demo completed")
}

func demoSlogLogger() {
	fmt.Println("\n=== Slog Logger Demo ===")

	handler, err := logbull.NewSlogHandler(logbull.Config{
		ProjectID: projectID,
		Host:      host,
		APIKey:    apiKey,
		LogLevel:  logbull.INFO,
	})
	if err != nil {
		panic(err)
	}
	defer handler.Shutdown()

	logger := slog.New(handler)

	logger.Info("[go - slog_logger - 1] Application started",
		slog.String("version", "1.0.0"),
		slog.String("environment", "production"),
	)

	logger.Warn("[go - slog_logger - 2] Rate limit approaching",
		slog.Int("current_requests", 950),
		slog.Int("limit", 1000),
		slog.String("user_id", "user_789"),
	)

	logger.Error("[go - slog_logger - 3] Database error",
		slog.Group("database",
			slog.String("query", "SELECT * FROM users"),
			slog.String("error", "Connection timeout"),
			slog.Int("retry_count", 3),
		),
	)

	logger.Info("[go - slog_logger - 4] User action",
		slog.String("user_id", "12345"),
		slog.Int("action_id", 42),
		slog.String("ip", "192.168.1.100"),
	)

	handler.Flush()
	fmt.Println("Slog Logger demo completed")
}

func demoZapLogger() {
	fmt.Println("\n=== Zap Logger Demo ===")

	core, err := logbull.NewZapCore(logbull.Config{
		ProjectID: projectID,
		Host:      host,
		APIKey:    apiKey,
		LogLevel:  logbull.INFO,
	})
	if err != nil {
		panic(err)
	}
	defer core.Shutdown()

	logger := zap.New(core)

	logger.Info("[go - zap_logger - 1] User action",
		zap.String("user_id", "12345"),
		zap.String("action", "login"),
		zap.String("ip", "192.168.1.100"),
	)

	logger.Error("[go - zap_logger - 2] Payment failed",
		zap.String("order_id", "ord_123"),
		zap.Float64("amount", 99.99),
		zap.String("currency", "USD"),
		zap.String("error_reason", "insufficient_funds"),
	)

	logger.Info("[go - zap_logger - 3] Request started",
		zap.String("request_id", "req_789"),
		zap.String("session_id", "sess_456"),
		zap.String("endpoint", "/api/users"),
	)

	logger.Warn("[go - zap_logger - 4] Cache miss",
		zap.String("cache_key", "user_profile_12345"),
		zap.String("fallback", "database_query"),
	)

	logger.Sync()
	fmt.Println("Zap Logger demo completed")
}

func demoLogrusLogger() {
	fmt.Println("\n=== Logrus Logger Demo ===")

	hook, err := logbull.NewLogrusHook(logbull.Config{
		ProjectID: projectID,
		Host:      host,
		APIKey:    apiKey,
		LogLevel:  logbull.INFO,
	})
	if err != nil {
		panic(err)
	}
	defer hook.Shutdown()

	logger := logrus.New()
	logger.AddHook(hook)

	logger.WithFields(logrus.Fields{
		"method":           "POST",
		"path":             "/api/users",
		"status_code":      201,
		"response_time_ms": 45,
	}).Info("[go - logrus_logger - 1] API request")

	logger.WithFields(logrus.Fields{
		"correlation_id":  "corr_123",
		"user_id":         "user_789",
		"amount":          150.00,
		"currency":        "EUR",
		"payment_gateway": "stripe",
	}).Info("[go - logrus_logger - 2] Processing payment")

	logger.WithFields(logrus.Fields{
		"correlation_id": "corr_123",
		"error_code":     "GATEWAY_TIMEOUT",
		"retry_count":    3,
		"max_retries":    5,
	}).Error("[go - logrus_logger - 3] Payment gateway error")

	logger.WithFields(logrus.Fields{
		"cpu_usage":          95.5,
		"memory_usage":       87.2,
		"active_connections": 1500,
	}).Error("[go - logrus_logger - 4] System overload detected")

	hook.Flush()
	fmt.Println("Logrus Logger demo completed")
}

func main() {
	fmt.Println("LogBull Go Library Demo")
	fmt.Println("========================================")

	// Demonstrate each logger type
	// Uncomment the ones you want to test

	demoLogBullLogger()
	demoSlogLogger()
	demoZapLogger()
	demoLogrusLogger()

	fmt.Println("\n=== Demo Complete ===")
	fmt.Println("Check your LogBull server for the logged messages!")

	// Wait for logs to be sent
	for i := 1; i <= 5; i++ {
		fmt.Printf("Waiting for %d seconds...\n", i)
		time.Sleep(1 * time.Second)
	}
}
