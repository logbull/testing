package com.logbull.test.service;

import com.logbull.LogBullLogger;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service demonstrating standalone LogBullLogger usage.
 */
@Service
public class StandaloneLoggerService {

        private final LogBullLogger logger;

        public StandaloneLoggerService(LogBullLogger logger) {
                this.logger = logger;
        }

        /**
         * Process an order and log with standalone logger.
         */
        public void processOrder(String orderId, String userId) {
                // Create a logger with context for this transaction
                LogBullLogger transactionLogger = logger.withContext(Map.of(
                                "order_id", orderId,
                                "user_id", userId,
                                "service", "order_processing"));

                transactionLogger.info("[java] Starting order processing");

                try {
                        // Simulate order validation
                        transactionLogger.debug("[java] Validating order", Map.of(
                                        "validation_step", "check_inventory"));

                        // Simulate payment processing
                        transactionLogger.info("[java] Processing payment", Map.of(
                                        "payment_method", "credit_card",
                                        "amount", 99.99));

                        // Simulate order completion
                        transactionLogger.info("[java] Order processed successfully", Map.of(
                                        "status", "completed",
                                        "processing_time_ms", 1250));

                } catch (Exception e) {
                        transactionLogger.error("[java] Order processing failed", Map.of(
                                        "error_type", e.getClass().getSimpleName(),
                                        "error_message", e.getMessage()));
                }
        }

        /**
         * Demonstrates context chaining with standalone logger.
         */
        public void logCombinedTest(String sessionId) {
                // Create session logger
                LogBullLogger sessionLogger = logger.withContext(Map.of(
                                "session_id", sessionId,
                                "test_type", "combined"));

                sessionLogger.info("[java] Combined test - Using Standalone Logger");

                // Add more context
                LogBullLogger detailedLogger = sessionLogger.withContext(Map.of(
                                "feature", "context_chaining",
                                "level", "nested"));

                detailedLogger.debug("[java] Demonstrating context chaining");
                detailedLogger.warning("[java] This is a warning with chained context", Map.of(
                                "warning_code", "W001",
                                "warning_type", "demonstration"));
        }

        /**
         * Demonstrates critical logging.
         */
        public void logCriticalEvent(String eventType, Map<String, Object> eventData) {
                logger.critical("[java] Critical event occurred", Map.of(
                                "event_type", eventType,
                                "event_data", eventData.toString(),
                                "requires_immediate_attention", true));
        }
}
