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
 * Tests for the SWAPI People resource: GET /api/people/
 *
 * <p>Covers: list all, get by ID, search, pagination, not found, schema validation.
 */
@Epic("SWAPI")
@Feature("People")
public class TestPeople extends BaseTest {

    // -------------------------------------------------------------------------
    // List People
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("List People")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get all people returns paginated results")
    void testGetAllPeople() {
        var data = swapiClient.getAllPeople();
        AssertionUtils.assertValidPagination(data);
    }

    @Test
    @Story("List People")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("People page size is at most 10")
    void testPeoplePageSize() {
        var data = swapiClient.getAllPeople();
        assertTrue(data.results.size() <= 10, "Default page should have at most 10 results");
    }

    @Test
    @Story("List People")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("People pagination returns different results per page")
    void testPeoplePagination() {
        var page1 = swapiClient.getAllPeople(1, null);
        var page2 = swapiClient.getAllPeople(2, null);

        assertNotNull(page2.previous, "Page 2 should have a 'previous' link");
        Set<String> page1Names = page1.results.stream()
                .map(p -> p.name).collect(Collectors.toSet());
        Set<String> page2Names = page2.results.stream()
                .map(p -> p.name).collect(Collectors.toSet());
        assertNotEquals(page1Names, page2Names, "Page 1 and 2 should have different people");
    }

    @Test
    @Story("List People")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Search people by name returns matching results")
    void testSearchPeople() {
        var data = swapiClient.getAllPeople(null, "luke");
        assertTrue(data.count >= 1, "Expected at least 1 result for search 'luke'");
        AssertionUtils.assertSearchResultsContain(data, p -> p.name, "luke", "name");
    }

    @Test
    @Story("List People")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Search people with nonsense term returns empty")
    void testSearchNoResults() {
        var data = swapiClient.getAllPeople(null, "xyznonexistent");
        assertEquals(0, data.count, "Expected zero results for nonsense search");
        assertTrue(data.results.isEmpty(), "Expected empty results list");
    }

    // -------------------------------------------------------------------------
    // Get Person by ID
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("Get Person by ID")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Get person by ID returns Luke Skywalker")
    void testGetPersonById() {
        var person = swapiClient.getPerson(1);
        assertEquals("Luke Skywalker", person.name);
        assertEquals("19BBY", person.birthYear);
    }

    @Test
    @Story("Get Person by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Person has a valid homeworld URL")
    void testPersonHasHomeworld() {
        var person = swapiClient.getPerson(1);
        assertTrue(person.homeworld.startsWith("http"),
                "Homeworld should be a URL starting with http");
        assertTrue(person.homeworld.contains("/api/planets/"),
                "Homeworld URL should reference /api/planets/");
    }

    @Test
    @Story("Get Person by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Person appears in at least one film")
    void testPersonHasFilms() {
        var person = swapiClient.getPerson(1);
        assertFalse(person.films.isEmpty(), "Luke Skywalker should appear in at least one film");
    }

    @Test
    @Story("Get Person by ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Person response passes schema validation")
    void testPersonSchemaValidation() {
        var person = swapiClient.getPerson(1);
        assertNotNull(person.name, "name should not be null");
        assertFalse(person.name.isBlank(), "name should not be blank");
        assertTrue(person.url.endsWith("/1/"), "url should end with /1/");
    }

    @Test
    @Story("Get Person by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-existent person returns 404")
    void testPersonNotFound() {
        var response = swapiClient.getPersonRaw(9999);
        AssertionUtils.assertStatusNotFound(response);
    }
}
