package com.kramp.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed configuration for per-service timeout budgets and simulated failure rates,
 * bound from the {@code aggregator.*} namespace in {@code application.yml}.
 */
@ConfigurationProperties(prefix = "aggregator")
@Getter
@Setter
public class AppProperties {

    private Timeouts timeouts = new Timeouts();
    private FailureRates failureRates = new FailureRates();

    @Getter
    @Setter
    public static class Timeouts {
        private long catalog = 500;
        private long pricing = 400;
        private long availability = 400;
        private long customer = 300;
    }

    @Getter
    @Setter
    public static class FailureRates {
        private double catalog = 0.001;
        private double pricing = 0.005;
        private double availability = 0.02;
        private double customer = 0.01;
    }
}
