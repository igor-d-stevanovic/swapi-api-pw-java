package com.swapi.utils;

import com.microsoft.playwright.APIResponse;
import com.swapi.clients.TimedResponse;
import com.swapi.config.Settings;
import com.swapi.models.PaginatedResponse;

/**
 * Reusable assertion helpers for SWAPI API tests.
 *
 * <p>These keep test classes clean and provide clear failure messages.
 */
public final class AssertionUtils {

    private AssertionUtils() {
    }

    // -------------------------------------------------------------------------
    // Status helpers
    // -------------------------------------------------------------------------

    /**
     * Asserts that the response has a 200 OK status.
     */
    public static void assertStatusOk(APIResponse response) {
        if (response.status() != 200) {
            throw new AssertionError(
                    "Expected status 200, got " + response.status() + ". URL: " + response.url());
        }
    }

    /**
     * Asserts that the response has a 404 Not Found status.
     */
    public static void assertStatusNotFound(APIResponse response) {
        if (response.status() != 404) {
            throw new AssertionError(
                    "Expected status 404, got " + response.status() + ". URL: " + response.url());
        }
    }

    // -------------------------------------------------------------------------
    // Response time helpers
    // -------------------------------------------------------------------------

    /**
     * Asserts that the response completed within the configured SLA
     * ({@code settings.maxResponseTimeMs}).
     */
    public static void assertResponseTime(TimedResponse timed) {
        assertResponseTime(timed, Settings.getInstance().maxResponseTimeMs);
    }

    /**
     * Asserts that the response completed within {@code maxMs} milliseconds.
     */
    public static void assertResponseTime(TimedResponse timed, double maxMs) {
        if (timed.elapsedMs() > maxMs) {
            throw new AssertionError(String.format(
                    "Response too slow: %.0f ms > %.0f ms. URL: %s",
                    timed.elapsedMs(), maxMs, timed.url()));
        }
    }

    // -------------------------------------------------------------------------
    // Pagination helpers
    // -------------------------------------------------------------------------

    /**
     * Asserts basic pagination invariants:
     * <ul>
     *   <li>count is at least {@code minCount}</li>
     *   <li>results list is not empty</li>
     *   <li>results length ≤ count (page is a subset of the total)</li>
     * </ul>
     */
    public static void assertValidPagination(PaginatedResponse<?> data, int minCount) {
        if (data.count < minCount) {
            throw new AssertionError("Expected count >= " + minCount + ", got " + data.count);
        }
        if (data.results.isEmpty()) {
            throw new AssertionError("Expected at least one result on the page");
        }
        if (data.results.size() > data.count) {
            throw new AssertionError(
                    "Page has " + data.results.size() + " items but total count is " + data.count);
        }
    }

    /**
     * Asserts basic pagination invariants with a default {@code minCount} of 1.
     */
    public static void assertValidPagination(PaginatedResponse<?> data) {
        assertValidPagination(data, 1);
    }

    /**
     * Asserts that every result on the page contains {@code term} (case-insensitive)
     * in the given field value.
     *
     * @param data      the paginated response
     * @param getField  a function that extracts the field value from each result
     * @param term      the search term to look for
     * @param fieldName the name of the field (for error messages)
     * @param <T>       the type of resource
     */
    public static <T> void assertSearchResultsContain(
            PaginatedResponse<T> data,
            java.util.function.Function<T, String> getField,
            String term,
            String fieldName) {
        String termLower = term.toLowerCase();
        for (T item : data.results) {
            String value = getField.apply(item);
            if (!value.toLowerCase().contains(termLower)) {
                throw new AssertionError(
                        "Expected '" + term + "' in '" + fieldName + "' but got '" + value + "'");
            }
        }
    }
}

