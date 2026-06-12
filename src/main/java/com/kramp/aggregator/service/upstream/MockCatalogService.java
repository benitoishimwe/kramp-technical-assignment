package com.kramp.aggregator.service.upstream;

import com.kramp.aggregator.config.AppProperties;
import com.kramp.aggregator.model.upstream.CatalogData;
import com.kramp.aggregator.service.port.CatalogServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock adapter for the Catalog upstream service. Product data (name, specs, images) is
 * derived deterministically from a hash of {@code productId} so the same input always
 * returns the same result — making tests and manual exploration reproducible.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MockCatalogService extends AbstractMockService implements CatalogServicePort {

    private static final long LATENCY_MS = 50;

    private static final String[] PRODUCT_TYPES = {
            "Hydraulic Filter", "Brake Pad Set", "Engine Oil Filter",
            "Transmission Belt", "Wheel Bearing Kit", "Air Filter", "Fuel Pump"
    };

    private final AppProperties properties;

    @Override
    public CatalogData fetch(String productId, String market) {
        log.debug("CatalogService: fetching productId={}, market={}", productId, market);
        simulateCall(LATENCY_MS, properties.getFailureRates().getCatalog(), "CatalogService");
        return generate(productId);
    }

    private CatalogData generate(String productId) {
        int seed = Math.abs(productId.hashCode());
        String type = PRODUCT_TYPES[seed % PRODUCT_TYPES.length];
        String sku  = "KR-" + (10000 + seed % 90000);
        String name = type + " " + sku;

        Map<String, String> specs = new LinkedHashMap<>();
        specs.put("weight",        (50 + seed % 950) + "g");
        specs.put("dimensions",    (seed % 200 + 50) + "x" + (seed % 100 + 20) + "x" + (seed % 50 + 10) + " mm");
        specs.put("material",      seed % 3 == 0 ? "Stainless Steel" : seed % 3 == 1 ? "Aluminium" : "Composite");
        specs.put("oemReference",  "OEM-" + (seed % 100_000));
        specs.put("compatibility", "Tractor, Harvester, Sprayer");

        List<String> images = List.of(
                "https://cdn.kramp.com/products/" + productId + "/main.jpg",
                "https://cdn.kramp.com/products/" + productId + "/side.jpg",
                "https://cdn.kramp.com/products/" + productId + "/detail.jpg"
        );

        String description = "High-quality " + name + " for agricultural and industrial machinery. "
                + "Meets OEM specifications. Part number: " + sku + ".";

        return new CatalogData(productId, name, description, specs, images);
    }
}
