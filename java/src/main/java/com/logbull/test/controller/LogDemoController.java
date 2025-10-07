package com.logbull.test.controller;

import com.logbull.test.service.StandaloneLoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Controller demonstrating both Spring Boot Starter (SLF4J) and Standalone
 * Logger usage.
 */
@RestController
@RequestMapping("/api/logs")
public class LogDemoController {

    private static final Logger logger = LoggerFactory.getLogger(LogDemoController.class);

    @Autowired
    private StandaloneLoggerService standaloneLoggerService;

    /**
     * Demonstrates Spring Boot Starter usage with SLF4J.
     * Logs are automatically sent to LogBull via the configured Spring Boot
     * Starter.
     */
    @GetMapping("/spring-boot-starter")
    public Map<String, String> testSpringBootStarter(
            @RequestParam(defaultValue = "john_doe") String username) {

        String requestId = UUID.randomUUID().toString();

        // Set MDC context for this request
        MDC.put("request_id", requestId);
        MDC.put("username", username);

        try {
            logger.info("[java] Testing Spring Boot Starter - Request received");
            logger.debug("[java] Processing user request with username: {}", username);

            // Simulate some processing
            if (username.equals("error")) {
                logger.error("[java] Simulated error for username: {}", username);
                return Map.of(
                        "status", "error",
                        "message", "Error occurred",
                        "request_id", requestId);
            }

            logger.info("[java] Request processed successfully for user: {}", username);

            return Map.of(
                    "status", "success",
                    "message", "Logs sent via Spring Boot Starter",
                    "username", username,
                    "request_id", requestId);

        } catch (Exception e) {
            logger.error("[java] Unexpected error", e);
            return Map.of(
                    "status", "error",
                    "message", e.getMessage(),
                    "request_id", requestId);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Demonstrates standalone LogBullLogger usage.
     * Uses the programmatically configured LogBullLogger instance.
     */
    @PostMapping("/standalone")
    public Map<String, String> testStandaloneLogger(
            @RequestBody Map<String, String> payload) {

        String orderId = payload.getOrDefault("order_id", UUID.randomUUID().toString());
        String userId = payload.getOrDefault("user_id", "user_123");

        // Use standalone logger service
        standaloneLoggerService.processOrder(orderId, userId);

        return Map.of(
                "status", "success",
                "message", "Logs sent via Standalone Logger",
                "order_id", orderId,
                "user_id", userId);
    }

    /**
     * Demonstrates both logging approaches in parallel.
     */
    @GetMapping("/combined")
    public Map<String, String> testBothApproaches() {
        String sessionId = UUID.randomUUID().toString();

        // Spring Boot Starter approach
        MDC.put("session_id", sessionId);
        logger.info("[java] Combined test - Using Spring Boot Starter");
        MDC.clear();

        // Standalone logger approach
        standaloneLoggerService.logCombinedTest(sessionId);

        return Map.of(
                "status", "success",
                "message", "Both logging approaches executed",
                "session_id", sessionId);
    }
}
