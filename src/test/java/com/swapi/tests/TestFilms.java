package com.swapi.tests;

import com.swapi.BaseTest;
import com.swapi.utils.AssertionUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the SWAPI Films resource: GET /api/films/
 *
 * <p>Covers: list all, get by ID, search, schema validation, not found.
 */
@Epic("SWAPI")
@Feature("Films")
public class TestFilms extends BaseTest {

    // -------------------------------------------------------------------------
    // List Films
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("List Films")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get all films returns paginated results")
    void testGetAllFilms() {
        var data = swapiClient.getAllFilms();
        AssertionUtils.assertValidPagination(data);
    }

    @Test
    @Story("List Films")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("SWAPI contains exactly 6 films")
    void testFilmsCount() {
        var data = swapiClient.getAllFilms();
        assertEquals(6, data.count, "SWAPI should have exactly 6 Star Wars films");
    }

    @Test
    @Story("List Films")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Search films by title returns matching results")
    void testSearchFilms() {
        var data = swapiClient.getAllFilms(null, "hope");
        assertTrue(data.count >= 1, "Expected at least 1 result for search 'hope'");
        AssertionUtils.assertSearchResultsContain(data, f -> f.title, "hope", "title");
    }

    @Test
    @Story("List Films")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Search films with nonsense term returns empty")
    void testSearchNoResults() {
        var data = swapiClient.getAllFilms(null, "xyznonexistent");
        assertEquals(0, data.count, "Expected zero results for nonsense search");
    }

    // -------------------------------------------------------------------------
    // Get Film by ID
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("Get Film by ID")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Get film by ID returns A New Hope")
    void testGetFilmById() {
        var film = swapiClient.getFilm(1);
        assertEquals("A New Hope", film.title);
        assertEquals(4, film.episodeId);
        assertEquals("George Lucas", film.director);
    }

    @Test
    @Story("Get Film by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Film has characters")
    void testFilmHasCharacters() {
        var film = swapiClient.getFilm(1);
        assertFalse(film.characters.isEmpty(), "A New Hope should have characters");
    }

    @Test
    @Story("Get Film by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Film has planets")
    void testFilmHasPlanets() {
        var film = swapiClient.getFilm(1);
        assertFalse(film.planets.isEmpty(), "A New Hope should reference planets");
    }

    @Test
    @Story("Get Film by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Film has correct release date")
    void testFilmReleaseDate() {
        var film = swapiClient.getFilm(1);
        assertEquals("1977-05-25", film.releaseDate);
    }

    @Test
    @Story("Get Film by ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Film response passes schema validation")
    void testFilmSchemaValidation() {
        var film = swapiClient.getFilm(1);
        assertNotNull(film.title, "title should not be null");
        assertFalse(film.title.isBlank(), "title should not be blank");
        assertTrue(film.url.endsWith("/1/"), "url should end with /1/");
    }

    @Test
    @Story("Get Film by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-existent film returns 404")
    void testFilmNotFound() {
        var response = swapiClient.getFilmRaw(9999);
        AssertionUtils.assertStatusNotFound(response);
    }
}
