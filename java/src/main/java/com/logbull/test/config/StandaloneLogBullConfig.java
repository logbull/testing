package com.logbull.test.config;

import com.logbull.LogBullLogger;
import com.logbull.core.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for standalone LogBullLogger.
 * This demonstrates how to configure LogBullLogger programmatically
 * alongside the Spring Boot Starter auto-configuration.
 */
@Configuration
public class StandaloneLogBullConfig {

    @Value("${logbull.project-id}")
    private String projectId;

    @Value("${logbull.host}")
    private String host;

    @Value("${logbull.api-key:}")
    private String apiKey;

    /**
     * Creates a standalone LogBullLogger bean.
     * This is separate from the Spring Boot Starter configuration
     * and demonstrates programmatic logger creation.
     */
    @Bean
    public LogBullLogger logBullLogger() {
        return LogBullLogger.builder()
                .projectId(projectId)
                .host(host)
                .apiKey(apiKey.isEmpty() ? null : apiKey)
                .logLevel(LogLevel.DEBUG)
                .build();
    }
}
