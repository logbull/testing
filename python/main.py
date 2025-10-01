import logging
import os
import structlog
import time
from dotenv import load_dotenv
from loguru import logger as loguru_logger
from logbull import LogBullLogger, LogBullHandler
from logbull.handlers import LoguruSink, StructlogProcessor


def setup_loggers():
    """Setup all four types of loggers"""

    # Load environment variables from parent directory (testing/.env)
    load_dotenv(dotenv_path="../.env")

    # Configuration from environment variables (with PYTHON_ prefix)
    PROJECT_ID = os.getenv("LOGBULL_PROJECT_ID")
    HOST = os.getenv("LOGBULL_HOST")
    API_KEY = os.getenv("LOGBULL_API_KEY", "")  # optional

    # Validate required variables
    if not PROJECT_ID:
        raise ValueError("LOGBULL_PROJECT_ID environment variable is required")
    if not HOST:
        raise ValueError("LOGBULL_HOST environment variable is required")

    # 1. LogBull Logger (standalone)
    logbull_logger = LogBullLogger(
        project_id=PROJECT_ID, host=HOST, api_key=API_KEY, log_level="DEBUG"
    )

    # 2. Standard Python Logger with LogBull Handler
    standard_logger = logging.getLogger("standard_logger")
    standard_logger.setLevel(logging.INFO)

    logbull_handler = LogBullHandler(project_id=PROJECT_ID, host=HOST, api_key=API_KEY)
    standard_logger.addHandler(logbull_handler)

    # 3. Loguru with LogBull Sink
    loguru_logger.add(
        LoguruSink(project_id=PROJECT_ID, host=HOST, api_key=API_KEY),
        level="INFO",
        format="{time} | {level} | {message}",
        serialize=True,
    )

    # 4. Structlog with LogBull Processor
    structlog.configure(
        processors=[
            structlog.contextvars.merge_contextvars,
            structlog.processors.TimeStamper(fmt="iso"),
            structlog.processors.add_log_level,
            StructlogProcessor(project_id=PROJECT_ID, host=HOST, api_key=API_KEY),
            structlog.processors.JSONRenderer(),
        ],
        wrapper_class=structlog.make_filtering_bound_logger(20),  # INFO level
        logger_factory=structlog.WriteLoggerFactory(),
        cache_logger_on_first_use=True,
    )

    structlog_logger = structlog.get_logger()

    return logbull_logger, standard_logger, loguru_logger, structlog_logger


def demo_logbull_logger(logger):
    """Demonstrate LogBull standalone logger"""
    print("\n=== LogBull Logger Demo ===")

    # Basic logging
    logger.info(
        "[python - logbull_logger - 1] User logged in successfully",
        fields={"user_id": "12345", "username": "john_doe", "ip": "192.168.1.100"},
    )

    logger.error(
        "[python - logbull_logger - 2] Database connection failed",
        fields={"database": "users_db", "error_code": 500},
    )

    logger.debug(
        "[python - logbull_logger - 3] Processing user data",
        fields={"step": "validation", "user_id": "12345"},
    )

    # Context management
    session_logger = logger.with_context(
        {"session_id": "sess_abc123", "user_id": "user_456", "request_id": "req_789"}
    )

    session_logger.info(
        "[python - logbull_logger - 4] User started checkout process",
        fields={"cart_items": 3, "total_amount": 149.99},
    )

    session_logger.warning(
        "[python - logbull_logger - 5] Payment processing slow",
        fields={"payment_method": "credit_card", "processing_time_ms": 5000},
    )


def demo_standard_logger(logger):
    """Demonstrate standard Python logging with LogBull handler"""
    print("\n=== Standard Python Logger Demo ===")

    logger.info(
        "[python - standard_logger - 1] Application started",
        extra={"version": "1.0.0", "environment": "production"},
    )

    logger.warning(
        "[python - standard_logger - 2] Rate limit approaching",
        extra={"current_requests": 950, "limit": 1000, "user_id": "user_789"},
    )

    logger.error(
        "[python - standard_logger - 3] Database error",
        extra={
            "query": "SELECT * FROM users",
            "error": "Connection timeout",
            "retry_count": 3,
        },
    )


def demo_loguru_logger(logger):
    """Demonstrate Loguru with LogBull sink"""
    print("\n=== Loguru Logger Demo ===")

    logger.info(
        "[python - loguru_logger - 1] User action",
        user_id=12345,
        action="login",
        ip="192.168.1.100",
    )

    logger.error(
        "[python - loguru_logger - 2] Payment failed",
        order_id="ord_123",
        amount=99.99,
        currency="USD",
        error_reason="insufficient_funds",
    )

    # Bind context for multiple logs
    bound_logger = logger.bind(request_id="req_789", session_id="sess_456")
    bound_logger.info("[python - loguru_logger - 3] Request started", endpoint="/api/users")
    bound_logger.info(
        "[python - loguru_logger - 3] Request completed", duration_ms=250, status_code=200
    )

    logger.warning(
        "[python - loguru_logger - 4] Cache miss",
        cache_key="user_profile_12345",
        fallback="database_query",
    )


def demo_structlog_logger(logger):
    """Demonstrate Structlog with LogBull processor"""
    print("\n=== Structlog Logger Demo ===")

    logger.info(
        "[python - structlog_logger - 1] API request",
        method="POST",
        path="/api/users",
        status_code=201,
        response_time_ms=45,
    )

    # With bound context
    bound_logger = logger.bind(correlation_id="corr_123", user_id="user_789")
    bound_logger.info(
        "[python - structlog_logger - 2] Processing payment",
        amount=150.00,
        currency="EUR",
        payment_gateway="stripe",
    )

    bound_logger.error(
        "[python - structlog_logger - 3] Payment gateway error",
        error_code="GATEWAY_TIMEOUT",
        retry_count=3,
        max_retries=5,
    )

    logger.critical(
        "[python - structlog_logger - 4] System overload detected",
        cpu_usage=95.5,
        memory_usage=87.2,
        active_connections=1500,
    )


def main():
    """Main function demonstrating all logging approaches"""
    print("LogBull Python Library Demo")
    print("=" * 40)

    try:
        # Setup all loggers
        logbull_logger, standard_logger, loguru_logger, structlog_logger = (
            setup_loggers()
        )

        # Demonstrate each logger type
        demo_logbull_logger(logbull_logger)
        demo_standard_logger(standard_logger)
        demo_loguru_logger(loguru_logger)
        demo_structlog_logger(structlog_logger)

        print("\n=== Demo Complete ===")
        print("Check your LogBull server for the logged messages!")

        for i in range(10):
            print(f"Waiting for {i} seconds...")
            time.sleep(1)

    except Exception as e:
        print(f"Error during demo: {e}")
        print("Make sure your LogBull server is running and configuration is correct.")


if __name__ == "__main__":
    main()
