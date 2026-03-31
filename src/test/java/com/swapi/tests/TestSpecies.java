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
 * Tests for the SWAPI Species resource: GET /api/species/
 *
 * <p>Covers: list all, get by ID, search, pagination, not found, schema validation.
 */
@Epic("SWAPI")
@Feature("Species")
public class TestSpecies extends BaseTest {

    // -------------------------------------------------------------------------
    // List Species
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("List Species")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get all species returns paginated results")
    void testGetAllSpecies() {
        var data = swapiClient.getAllSpecies();
        AssertionUtils.assertValidPagination(data);
    }

    @Test
    @Story("List Species")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Species page size is at most 10")
    void testSpeciesPageSize() {
        var data = swapiClient.getAllSpecies();
        assertTrue(data.results.size() <= 10, "Default page should have at most 10 results");
    }

    @Test
    @Story("List Species")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Species pagination returns different results per page")
    void testSpeciesPagination() {
        var page1 = swapiClient.getAllSpecies(1, null);
        var page2 = swapiClient.getAllSpecies(2, null);

        assertNotNull(page2.previous, "Page 2 should have a 'previous' link");
        Set<String> page1Names = page1.results.stream()
                .map(s -> s.name).collect(Collectors.toSet());
        Set<String> page2Names = page2.results.stream()
                .map(s -> s.name).collect(Collectors.toSet());
        assertNotEquals(page1Names, page2Names, "Page 1 and 2 should have different species");
    }

    @Test
    @Story("List Species")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Search species by name returns matching results")
    void testSearchSpecies() {
        var data = swapiClient.getAllSpecies(null, "human");
        assertTrue(data.count >= 1, "Expected at least 1 result for search 'human'");
        AssertionUtils.assertSearchResultsContain(data, s -> s.name, "human", "name");
    }

    @Test
    @Story("List Species")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Search species with nonsense term returns empty")
    void testSearchNoResults() {
        var data = swapiClient.getAllSpecies(null, "xyznonexistent");
        assertEquals(0, data.count, "Expected zero results for nonsense search");
    }

    // -------------------------------------------------------------------------
    // Get Species by ID
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("Get Species by ID")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Get species by ID returns Human")
    void testGetSpeciesById() {
        var species = swapiClient.getSpecies(1);
        assertEquals("Human", species.name);
        assertEquals("mammal", species.classification);
        assertEquals("sentient", species.designation);
    }

    @Test
    @Story("Get Species by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Species has people")
    void testSpeciesHasPeople() {
        var species = swapiClient.getSpecies(1);
        assertFalse(species.people.isEmpty(), "Human species should list people");
    }

    @Test
    @Story("Get Species by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Species has correct language")
    void testSpeciesLanguage() {
        var species = swapiClient.getSpecies(1);
        assertEquals("Galactic Basic", species.language, "Humans speak Galactic Basic");
    }

    @Test
    @Story("Get Species by ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Species response passes schema validation")
    void testSpeciesSchemaValidation() {
        var species = swapiClient.getSpecies(1);
        assertNotNull(species.name, "name should not be null");
        assertFalse(species.name.isBlank(), "name should not be blank");
        assertTrue(species.url.endsWith("/1/"), "url should end with /1/");
    }

    @Test
    @Story("Get Species by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-existent species returns 404")
    void testSpeciesNotFound() {
        var response = swapiClient.getSpeciesRaw(9999);
        AssertionUtils.assertStatusNotFound(response);
    }
}
