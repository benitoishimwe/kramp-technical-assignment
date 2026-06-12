package com.kramp.aggregator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Provisions a dedicated thread pool for upstream I/O so that slow or timing-out
 * service calls never block HTTP/gRPC handler threads. Uses a caller-runs rejection
 * policy to avoid silently dropping requests under saturation.
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "aggregatorExecutor")
    public Executor aggregatorExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("aggregator-");
        executor.setKeepAliveSeconds(60);
        // Caller-runs under saturation — avoids dropping requests silently.
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
