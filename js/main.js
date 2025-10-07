import { config } from "dotenv";
import {
  LogBullLogger,
  LogLevel,
  LogBullTransport,
  createPinoTransport,
} from "logbull";
import winston from "winston";
import pino from "pino";

// Load environment variables from parent directory
config({ path: "../.env" });

// Configuration from environment variables
const PROJECT_ID = process.env.LOGBULL_PROJECT_ID;
const HOST = process.env.LOGBULL_HOST;
const API_KEY = process.env.LOGBULL_API_KEY || "";

// Validate required variables
if (!PROJECT_ID) {
  throw new Error("LOGBULL_PROJECT_ID environment variable is required");
}
if (!HOST) {
  throw new Error("LOGBULL_HOST environment variable is required");
}

function demoLogBullLogger() {
  const logger = new LogBullLogger({
    host: HOST,
    projectId: PROJECT_ID,
    apiKey: API_KEY,
    logLevel: LogLevel.DEBUG,
  });

  // Basic logging
  logger.info("[js - logbull_logger - 1] User logged in successfully", {
    user_id: "12345",
    username: "john_doe",
    ip: "192.168.1.100",
  });

  logger.error("[js - logbull_logger - 2] Database connection failed", {
    database: "users_db",
    error_code: 500,
  });

  logger.debug("[js - logbull_logger - 3] Processing user data", {
    step: "validation",
    user_id: "12345",
  });

  // Context management
  const sessionLogger = logger.withContext({
    session_id: "sess_abc123",
    user_id: "user_456",
    request_id: "req_789",
  });

  sessionLogger.info(
    "[js - logbull_logger - 4] User started checkout process",
    {
      cart_items: 3,
      total_amount: 149.99,
    }
  );

  sessionLogger.warning("[js - logbull_logger - 5] Payment processing slow", {
    payment_method: "credit_card",
    processing_time_ms: 5000,
  });

  logger.flush();

  return logger;
}

function demoWinstonLogger() {
  const logger = winston.createLogger({
    level: "info",
    format: winston.format.json(),
    transports: [
      new LogBullTransport({
        host: HOST,
        projectId: PROJECT_ID,
        apiKey: API_KEY,
      }),
    ],
  });

  logger.info("[js - winston_logger - 1] Application started", {
    version: "1.0.0",
    environment: "production",
  });

  logger.warn("[js - winston_logger - 2] Rate limit approaching", {
    current_requests: 950,
    limit: 1000,
    user_id: "user_789",
  });

  logger.error("[js - winston_logger - 3] Database error", {
    query: "SELECT * FROM users",
    error: "Connection timeout",
    retry_count: 3,
  });

  // Winston child logger (context)
  const requestLogger = logger.child({
    request_id: "req_789",
    session_id: "sess_456",
  });

  requestLogger.info("[js - winston_logger - 4] Request started", {
    endpoint: "/api/users",
  });

  requestLogger.info("[js - winston_logger - 5] Request completed", {
    duration_ms: 250,
    status_code: 200,
  });

  // Flush the transport
  const transport = logger.transports.find(
    (t) => t instanceof LogBullTransport
  );
  if (transport) {
    transport.flush();
  }

  return transport;
}

function demoPinoLogger() {
  const transport = createPinoTransport({
    host: HOST,
    projectId: PROJECT_ID,
    apiKey: API_KEY,
  });

  const logger = pino(
    {
      level: "info",
    },
    transport
  );

  logger.info(
    {
      user_id: "12345",
      action: "login",
      ip: "192.168.1.100",
    },
    "[js - pino_logger - 1] User action"
  );

  logger.error(
    {
      order_id: "ord_123",
      amount: 99.99,
      currency: "USD",
      error_reason: "insufficient_funds",
    },
    "[js - pino_logger - 2] Payment failed"
  );

  // Pino child logger (context)
  const requestLogger = logger.child({
    request_id: "req_789",
    session_id: "sess_456",
  });

  requestLogger.info(
    {
      endpoint: "/api/users",
    },
    "[js - pino_logger - 3] Request started"
  );

  requestLogger.info(
    {
      duration_ms: 250,
      status_code: 200,
    },
    "[js - pino_logger - 4] Request completed"
  );

  logger.warn(
    {
      cache_key: "user_profile_12345",
      fallback: "database_query",
    },
    "[js - pino_logger - 5] Cache miss"
  );

  // Flush the transport
  transport.transport.flush();

  // Return the transport for shutdown handling
  return transport;
}

async function main() {
  let logbullLogger;
  let winstonTransport;
  let pinoTransport;

  try {
    // Demonstrate each logger type
    logbullLogger = demoLogBullLogger();
    winstonTransport = demoWinstonLogger();
    pinoTransport = demoPinoLogger();

    // Wait for logs to be sent
    for (let i = 1; i <= 5; i++) {
      await new Promise((resolve) => setTimeout(resolve, 1000));
    }

    // Cleanup
    if (logbullLogger) {
      await logbullLogger.shutdown();
    }
    if (winstonTransport) {
      await winstonTransport.shutdown();
    }
    if (pinoTransport) {
      await pinoTransport.transport.shutdown();
    }
  } catch (error) {
    console.error("Error during demo:", error);
    process.exit(1);
  }
}

// Handle graceful shutdown
process.on("SIGINT", async () => {
  process.exit(0);
});

process.on("SIGTERM", async () => {
  process.exit(0);
});

main();
