package com.swapi.tests;

import com.swapi.BaseTest;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the SWAPI root endpoint: GET /api/
 *
 * <p>The root endpoint returns a JSON object mapping each resource name
 * to its list URL.
 */
@Epic("SWAPI")
@Feature("Root Endpoint")
public class TestRoot extends BaseTest {

    private static final Set<String> EXPECTED_RESOURCES =
            Set.of("people", "planets", "films", "species", "vehicles", "starships");

    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Root endpoint returns HTTP 200")
    void testRootReturns200() {
        var response = apiContext.get("/api/");
        assertEquals(200, response.status(), "Expected HTTP 200 from root endpoint");
    }

    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Root response lists all six resources")
    void testRootContainsAllResources() {
        var data = swapiClient.getRoot();
        assertEquals(EXPECTED_RESOURCES, data.keySet(),
                "Missing or extra resources: " + symmetricDiff(EXPECTED_RESOURCES, data.keySet()));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Root resource URLs are valid")
    void testRootValuesAreValidUrls() {
        var data = swapiClient.getRoot();
        for (var entry : data.entrySet()) {
            assertTrue(entry.getValue().startsWith("http"),
                    "Resource '" + entry.getKey() + "' has invalid URL: " + entry.getValue());
            assertTrue(entry.getValue().contains("/api/"),
                    "Resource '" + entry.getKey() + "' URL missing /api/ path: " + entry.getValue());
        }
    }

    private static <T> Set<T> symmetricDiff(Set<T> a, Set<T> b) {
        var diff = new java.util.HashSet<>(a);
        diff.addAll(b);
        var intersection = new java.util.HashSet<>(a);
        intersection.retainAll(b);
        diff.removeAll(intersection);
        return diff;
    }
}
