package com.kramp.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.kramp.aggregator.config.AppProperties;

/**
 * Spring Boot entry point. Enables typed configuration binding via
 * {@link AppProperties} and starts the embedded Tomcat (REST) and gRPC servers.
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class AggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApplication.class, args);
    }
}
