package com.swapi.tests;

import com.swapi.BaseTest;
import com.swapi.utils.AssertionUtils;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Negative and edge-case tests for SWAPI endpoints.
 *
 * <p>Covers: malformed params, special characters, boundary values, empty search.
 */
@Epic("SWAPI")
@Feature("Edge Cases")
@Story("Negative Testing")
public class TestNegative extends BaseTest {

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Invalid page parameter returns 404")
    void testInvalidPageParam() {
        var response = apiContext.get("/api/people/",
                RequestOptions.create().setQueryParam("page", "0"));
        assertEquals(404, response.status(), "page=0 should return 404");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-numeric page parameter returns 404")
    void testNonNumericPage() {
        var response = apiContext.get("/api/people/",
                RequestOptions.create().setQueryParam("page", "abc"));
        assertEquals(404, response.status(), "page=abc should return 404");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Empty search term returns all results")
    void testEmptySearch() {
        var data = swapiClient.getAllPeople(null, "");
        assertTrue(data.count > 0, "Empty search should return all results");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Search with special characters returns empty")
    void testSpecialCharsSearch() {
        var data = swapiClient.getAllPeople(null, "<script>alert(1)</script>");
        assertEquals(0, data.count, "Special-character search should return zero results");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Very large page number returns 404")
    void testVeryLargePage() {
        var response = apiContext.get("/api/people/",
                RequestOptions.create().setQueryParam("page", "999999"));
        AssertionUtils.assertStatusNotFound(response);
    }

    @ParameterizedTest(name = "{0}-9999 returns 404")
    @ValueSource(strings = {"people", "planets", "films", "species", "vehicles", "starships"})
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-existent resource ID 9999 returns 404")
    void testNotFoundAllResources(String resource) {
        var response = apiContext.get("/api/" + resource + "/9999/");
        AssertionUtils.assertStatusNotFound(response);
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Trailing-slash-less URL redirects or works")
    void testNoTrailingSlash() {
        var response = apiContext.get("/api/people/1");
        assertTrue(response.status() == 200 || response.status() == 301 || response.status() == 302,
                "URL without trailing slash should return 200, 301, or 302");
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Non-existent endpoint returns 404")
    void testInvalidEndpoint() {
        var response = apiContext.get("/api/nonexistent/");
        AssertionUtils.assertStatusNotFound(response);
    }
}
