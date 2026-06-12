package com.kramp.aggregator.service.upstream;

import com.kramp.aggregator.exception.UpstreamServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Base class for mock upstream adapters. Simulates realistic network conditions
 * by sleeping for {@code baseLatencyMs ± 20%} and throwing
 * {@link com.kramp.aggregator.exception.UpstreamServiceException} at a configurable
 * failure rate, so the aggregation layer can be exercised end-to-end without real services.
 */
@Slf4j
public abstract class AbstractMockService {

    protected void simulateCall(long baseLatencyMs, double failureRate, String serviceName) {
        long jitter = (long) (baseLatencyMs * 0.2);
        long delay = baseLatencyMs + ThreadLocalRandom.current().nextLong(-jitter, jitter + 1);
        try {
            Thread.sleep(Math.max(1, delay));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UpstreamServiceException(serviceName + " interrupted during simulated latency");
        }

        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            log.warn("{} simulated failure (rate: {}%)", serviceName,
                    String.format("%.1f", failureRate * 100));
            throw new UpstreamServiceException(serviceName + " transient failure");
        }
    }
}
