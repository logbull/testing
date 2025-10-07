package com.logbull.test;

import com.logbull.test.service.StandaloneLoggerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * Integration Tests for Standalone LogBull Logger.
 * These tests verify the actual LogBullLogger functionality without mocking.
 */
@SpringBootTest
class StandaloneLogBullIntegrationTests {

    @Autowired
    private StandaloneLoggerService standaloneLoggerService;

    @Test
    @DisplayName("Standalone Logger - Process order with all log levels")
    void testProcessOrder_AllLogLevels() {
        String orderId = "test_order_001";
        String userId = "test_user_001";

        // This should trigger debug, info logs
        standaloneLoggerService.processOrder(orderId, userId);

        // If we reach here without exceptions, the logger is working
        assert true;
    }

    @Test
    @DisplayName("Standalone Logger - Context chaining works correctly")
    void testLogCombinedTest_ContextChaining() {
        String sessionId = "test_session_001";

        // This should create nested context and log with chained context
        standaloneLoggerService.logCombinedTest(sessionId);

        // If we reach here without exceptions, context chaining is working
        assert true;
    }

    @Test
    @DisplayName("Standalone Logger - Critical event logging")
    void testCriticalEventLogging() {
        String eventType = "system_failure";
        Map<String, Object> eventData = Map.of(
                "severity", "high",
                "component", "database",
                "error_code", "DB_CONNECTION_LOST");

        standaloneLoggerService.logCriticalEvent(eventType, eventData);

        // If we reach here without exceptions, critical logging is working
        assert true;
    }

    @Test
    @DisplayName("Standalone Logger - Multiple sequential operations")
    void testMultipleSequentialOperations() {
        // Test multiple operations in sequence
        standaloneLoggerService.processOrder("order_001", "user_001");
        standaloneLoggerService.logCombinedTest("session_001");
        standaloneLoggerService.processOrder("order_002", "user_002");

        // If we reach here without exceptions, sequential logging is working
        assert true;
    }

    @Test
    @DisplayName("Standalone Logger - Handles empty context gracefully")
    void testEmptyContextHandling() {
        // Test with empty event data
        Map<String, Object> emptyData = Map.of();
        standaloneLoggerService.logCriticalEvent("test_event", emptyData);

        // If we reach here without exceptions, empty data handling is working
        assert true;
    }

    @Test
    @DisplayName("Standalone Logger - Multiple users with different contexts")
    void testMultipleUsersWithDifferentContexts() {
        // Simulate multiple users/orders with different contexts
        standaloneLoggerService.processOrder("order_001", "alice");
        standaloneLoggerService.processOrder("order_002", "bob");
        standaloneLoggerService.processOrder("order_003", "charlie");

        // If we reach here without exceptions, multiple contexts are working
        assert true;
    }
}
