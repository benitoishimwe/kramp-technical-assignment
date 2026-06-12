package com.kramp.aggregator.service.upstream;

import com.kramp.aggregator.config.AppProperties;
import com.kramp.aggregator.model.upstream.AvailabilityData;
import com.kramp.aggregator.service.port.AvailabilityServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mock adapter for the Availability upstream service. Warehouse location is derived
 * from the market country code; delivery days widen to 7–21 when stock is zero,
 * reflecting realistic back-order behaviour.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MockAvailabilityService extends AbstractMockService implements AvailabilityServicePort {

    private static final long LATENCY_MS = 100;

    private final AppProperties properties;

    @Override
    public AvailabilityData fetch(String productId, String market) {
        log.debug("AvailabilityService: fetching productId={}, market={}", productId, market);
        simulateCall(LATENCY_MS, properties.getFailureRates().getAvailability(), "AvailabilityService");

        int seed       = Math.abs(productId.hashCode());
        int stockLevel = seed % 200;

        String countryCode = market.length() >= 5 ? market.substring(3).toUpperCase() : "EU";
        String warehouse   = countryCode + "-WH-" + (1 + seed % 3);

        int deliveryDays = (stockLevel > 0) ? (1 + seed % 4) : (7 + seed % 14);

        return new AvailabilityData(stockLevel, warehouse, deliveryDays);
    }
}
