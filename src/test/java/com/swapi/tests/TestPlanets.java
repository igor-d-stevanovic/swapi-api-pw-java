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
 * Tests for the SWAPI Planets resource: GET /api/planets/
 *
 * <p>Covers: list all, get by ID, search, pagination, not found, schema validation.
 */
@Epic("SWAPI")
@Feature("Planets")
public class TestPlanets extends BaseTest {

    // -------------------------------------------------------------------------
    // List Planets
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("List Planets")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get all planets returns paginated results")
    void testGetAllPlanets() {
        var data = swapiClient.getAllPlanets();
        AssertionUtils.assertValidPagination(data);
    }

    @Test
    @Story("List Planets")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Planets page size is at most 10")
    void testPlanetsPageSize() {
        var data = swapiClient.getAllPlanets();
        assertTrue(data.results.size() <= 10, "Default page should have at most 10 results");
    }

    @Test
    @Story("List Planets")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Planets pagination returns different results per page")
    void testPlanetsPagination() {
        var page1 = swapiClient.getAllPlanets(1, null);
        var page2 = swapiClient.getAllPlanets(2, null);

        assertNotNull(page2.previous, "Page 2 should have a 'previous' link");
        Set<String> page1Names = page1.results.stream()
                .map(p -> p.name).collect(Collectors.toSet());
        Set<String> page2Names = page2.results.stream()
                .map(p -> p.name).collect(Collectors.toSet());
        assertNotEquals(page1Names, page2Names, "Page 1 and 2 should have different planets");
    }

    @Test
    @Story("List Planets")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Search planets by name returns matching results")
    void testSearchPlanets() {
        var data = swapiClient.getAllPlanets(null, "tatooine");
        assertTrue(data.count >= 1, "Expected at least 1 result for search 'tatooine'");
        AssertionUtils.assertSearchResultsContain(data, p -> p.name, "tatooine", "name");
    }

    @Test
    @Story("List Planets")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Search planets with nonsense term returns empty")
    void testSearchNoResults() {
        var data = swapiClient.getAllPlanets(null, "xyznonexistent");
        assertEquals(0, data.count, "Expected zero results for nonsense search");
    }

    // -------------------------------------------------------------------------
    // Get Planet by ID
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("Get Planet by ID")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Get planet by ID returns Tatooine")
    void testGetPlanetById() {
        var planet = swapiClient.getPlanet(1);
        assertEquals("Tatooine", planet.name);
        assertEquals("arid", planet.climate);
        assertEquals("desert", planet.terrain);
    }

    @Test
    @Story("Get Planet by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Planet has residents")
    void testPlanetHasResidents() {
        var planet = swapiClient.getPlanet(1);
        assertFalse(planet.residents.isEmpty(), "Tatooine should have at least one resident");
    }

    @Test
    @Story("Get Planet by ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Planet response passes schema validation")
    void testPlanetSchemaValidation() {
        var planet = swapiClient.getPlanet(1);
        assertNotNull(planet.name, "name should not be null");
        assertFalse(planet.name.isBlank(), "name should not be blank");
        assertTrue(planet.url.endsWith("/1/"), "url should end with /1/");
    }

    @Test
    @Story("Get Planet by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-existent planet returns 404")
    void testPlanetNotFound() {
        var response = swapiClient.getPlanetRaw(9999);
        AssertionUtils.assertStatusNotFound(response);
    }
}
