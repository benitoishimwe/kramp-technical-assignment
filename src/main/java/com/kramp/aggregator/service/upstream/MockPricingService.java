package com.kramp.aggregator.service.upstream;

import com.kramp.aggregator.config.AppProperties;
import com.kramp.aggregator.model.upstream.PricingData;
import com.kramp.aggregator.service.port.PricingServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Mock adapter for the Pricing upstream service. Base price is derived from a hash of
 * {@code productId}; authenticated customers receive a 10% discount (15% for every third
 * customer segment), simulating personalised B2B pricing tiers.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MockPricingService extends AbstractMockService implements PricingServicePort {

    private static final long LATENCY_MS = 80;

    private final AppProperties properties;

    @Override
    public PricingData fetch(String productId, String market, String customerId) {
        log.debug("PricingService: fetching productId={}, market={}, customerId={}",
                productId, market, customerId);
        simulateCall(LATENCY_MS, properties.getFailureRates().getPricing(), "PricingService");

        int seed = Math.abs(productId.hashCode());
        double basePrice = round2(10.0 + (seed % 990));

        double discountRate = 0.0;
        if (StringUtils.hasText(customerId)) {
            int customerSeed = Math.abs(customerId.hashCode());
            discountRate = (customerSeed % 3 == 0) ? 0.15 : 0.10;
        }

        double discount   = round2(basePrice * discountRate);
        double finalPrice = round2(basePrice - discount);

        return new PricingData(basePrice, discount, finalPrice);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
