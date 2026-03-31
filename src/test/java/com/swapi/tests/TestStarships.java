package com.swapi.tests;

import com.swapi.BaseTest;
import com.swapi.utils.AssertionUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the SWAPI Starships resource: GET /api/starships/
 *
 * <p>Covers: list all, get by ID, search, pagination, not found, schema validation.
 */
@Epic("SWAPI")
@Feature("Starships")
public class TestStarships extends BaseTest {

    // -------------------------------------------------------------------------
    // List Starships
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("List Starships")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get all starships returns paginated results")
    void testGetAllStarships() {
        var data = swapiClient.getAllStarships();
        AssertionUtils.assertValidPagination(data);
    }

    @Test
    @Story("List Starships")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Starships page size is at most 10")
    void testStarshipsPageSize() {
        var data = swapiClient.getAllStarships();
        assertTrue(data.results.size() <= 10, "Default page should have at most 10 results");
    }

    @Test
    @Story("List Starships")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Starships pagination returns different results per page")
    void testStarshipsPagination() {
        var page1 = swapiClient.getAllStarships(1, null);
        var page2 = swapiClient.getAllStarships(2, null);

        assertNotNull(page2.previous, "Page 2 should have a 'previous' link");
        Set<String> page1Names = page1.results.stream()
                .map(s -> s.name).collect(Collectors.toSet());
        Set<String> page2Names = page2.results.stream()
                .map(s -> s.name).collect(Collectors.toSet());
        assertNotEquals(page1Names, page2Names, "Page 1 and 2 should have different starships");
    }

    @Test
    @Story("List Starships")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Search starships by name returns matching results")
    void testSearchStarships() {
        var data = swapiClient.getAllStarships(null, "death");
        assertTrue(data.count >= 1, "Expected at least 1 result for search 'death'");
        AssertionUtils.assertSearchResultsContain(data, s -> s.name, "death", "name");
    }

    @Test
    @Story("List Starships")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Search starships with nonsense term returns empty")
    void testSearchNoResults() {
        var data = swapiClient.getAllStarships(null, "xyznonexistent");
        assertEquals(0, data.count, "Expected zero results for nonsense search");
    }

    // -------------------------------------------------------------------------
    // Get Starship by ID
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("Get Starship by ID")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Get starship by ID returns Death Star")
    void testGetStarshipById() {
        var starship = swapiClient.getStarship(9);
        assertEquals("Death Star", starship.name);
        assertEquals("DS-1 Orbital Battle Station", starship.model);
    }

    @Test
    @Story("Get Starship by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Starship has correct manufacturer")
    void testStarshipManufacturer() {
        var starship = swapiClient.getStarship(9);
        assertTrue(starship.manufacturer.contains("Imperial Department of Military Research"),
                "Death Star manufacturer should include Imperial Department of Military Research");
    }

    @Test
    @Story("Get Starship by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Starship appears in at least one film")
    void testStarshipHasFilms() {
        var starship = swapiClient.getStarship(9);
        assertFalse(starship.films.isEmpty(), "Death Star should appear in at least one film");
    }

    @Test
    @Story("Get Starship by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Starship has a hyperdrive rating")
    void testStarshipHyperdrive() {
        var starship = swapiClient.getStarship(9);
        assertNotEquals("unknown", starship.hyperdriveRating,
                "Death Star should have a known hyperdrive rating");
    }

    @Test
    @Story("Get Starship by ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Starship response passes schema validation")
    void testStarshipSchemaValidation() {
        var starship = swapiClient.getStarship(9);
        assertNotNull(starship.name, "name should not be null");
        assertFalse(starship.name.isBlank(), "name should not be blank");
        assertTrue(starship.url.endsWith("/9/"), "url should end with /9/");
    }

    @Test
    @Story("Get Starship by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-existent starship returns 404")
    void testStarshipNotFound() {
        var response = swapiClient.getStarshipRaw(9999);
        AssertionUtils.assertStatusNotFound(response);
    }
}
