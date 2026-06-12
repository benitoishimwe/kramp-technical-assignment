package com.kramp.aggregator.service.upstream;

import com.kramp.aggregator.config.AppProperties;
import com.kramp.aggregator.model.upstream.CustomerData;
import com.kramp.aggregator.service.port.CustomerServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mock adapter for the Customer upstream service. Segment and preferences are
 * hash-derived from {@code customerId}, so the same customer always returns the
 * same profile — enabling deterministic personalisation tests without a real CRM.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MockCustomerService extends AbstractMockService implements CustomerServicePort {

    private static final long LATENCY_MS = 60;

    private static final String[] SEGMENTS = {
            "STANDARD", "PRO", "PREMIUM", "DEALER", "ENTERPRISE"
    };

    private final AppProperties properties;

    @Override
    public CustomerData fetch(String customerId) {
        log.debug("CustomerService: fetching customerId={}", customerId);
        simulateCall(LATENCY_MS, properties.getFailureRates().getCustomer(), "CustomerService");

        int seed    = Math.abs(customerId.hashCode());
        String segment = SEGMENTS[seed % SEGMENTS.length];

        List<String> preferences = new ArrayList<>();
        if (seed % 2 == 0) preferences.add("fast-delivery");
        if (seed % 3 == 0) preferences.add("invoice-payment");
        if (seed % 5 == 0) preferences.add("bulk-discount");
        if (seed % 7 == 0) preferences.add("technical-support");

        return new CustomerData(customerId, segment, Collections.unmodifiableList(preferences));
    }
}
