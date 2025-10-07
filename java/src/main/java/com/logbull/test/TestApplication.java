package com.logbull.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LogBull Test Application
 * 
 * Demonstrates two ways to use LogBull in a Spring Boot application:
 * 1. Spring Boot Starter - Auto-configured SLF4J integration
 * 2. Standalone Logger - Programmatically configured LogBullLogger
 * 
 * Available endpoints:
 * - GET /api/logs/spring-boot-starter?username=john_doe
 * - POST /api/logs/standalone (with JSON body: {"order_id": "...", "user_id":
 * "..."})
 * - GET /api/logs/combined
 * 
 * Configuration in application.properties:
 * - logbull.enabled=true
 * - logbull.project-id=12345678-1234-1234-1234-123456789012
 * - logbull.host=http://localhost:4005
 * - logbull.api-key=your-api-key
 * - logbull.log-level=INFO
 */
@SpringBootApplication
public class TestApplication {

	private static final Logger logger = LoggerFactory.getLogger(TestApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
		logger.info("[java] LogBull Test Application started successfully");
		logger.info("[java] Available endpoints:");
		logger.info("[java]   GET  /api/logs/spring-boot-starter?username=john_doe");
		logger.info("[java]   POST /api/logs/standalone");
		logger.info("[java]   GET  /api/logs/combined");
	}

}
