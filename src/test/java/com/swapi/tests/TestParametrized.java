package com.swapi.tests;

import com.swapi.BaseTest;
import com.swapi.utils.AssertionUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parametrized tests — data-driven tests across multiple IDs for each resource.
 *
 * <p>Uses {@link ParameterizedTest} to test several known entities per resource
 * in a single test definition, reducing duplication.
 */
@Epic("SWAPI")
public class TestParametrized extends BaseTest {

    // -------------------------------------------------------------------------
    // People
    // -------------------------------------------------------------------------

    @Feature("People")
    @Story("Parametrized")
    @ParameterizedTest(name = "person {0} is {1}")
    @CsvSource({
            "1, Luke Skywalker",
            "2, C-3PO",
            "3, R2-D2",
            "4, Darth Vader",
            "5, Leia Organa"
    })
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get person by ID returns expected name")
    void testGetPersonById(int personId, String expectedName) {
        var person = swapiClient.getPerson(personId);
        assertEquals(expectedName, person.name,
                "Person " + personId + " should be '" + expectedName + "'");
    }

    @Feature("People")
    @Story("Parametrized")
    @ParameterizedTest(name = "person {0} responds within SLA")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Person responds within SLA")
    void testPersonResponseTime(int personId) {
        var timed = swapiClient.getTimed("/api/people/" + personId + "/");
        AssertionUtils.assertResponseTime(timed);
    }

    // -------------------------------------------------------------------------
    // Planets
    // -------------------------------------------------------------------------

    @Feature("Planets")
    @Story("Parametrized")
    @ParameterizedTest(name = "planet {0} is {1}")
    @CsvSource({
            "1, Tatooine",
            "2, Alderaan",
            "3, Yavin IV",
            "5, Dagobah",
            "8, Naboo"
    })
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get planet by ID returns expected name")
    void testGetPlanetById(int planetId, String expectedName) {
        var planet = swapiClient.getPlanet(planetId);
        assertEquals(expectedName, planet.name,
                "Planet " + planetId + " should be '" + expectedName + "'");
    }

    // -------------------------------------------------------------------------
    // Films
    // -------------------------------------------------------------------------

    @Feature("Films")
    @Story("Parametrized")
    @ParameterizedTest(name = "film {0} is episode {2}: {1}")
    @CsvSource({
            "1, A New Hope, 4",
            "2, The Empire Strikes Back, 5",
            "3, Return of the Jedi, 6",
            "4, The Phantom Menace, 1",
            "5, Attack of the Clones, 2",
            "6, Revenge of the Sith, 3"
    })
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get film by ID returns expected title and episode")
    void testGetFilmById(int filmId, String expectedTitle, int expectedEpisode) {
        var film = swapiClient.getFilm(filmId);
        assertEquals(expectedTitle, film.title,
                "Film " + filmId + " should be '" + expectedTitle + "'");
        assertEquals(expectedEpisode, film.episodeId,
                "Film " + filmId + " should have episode ID " + expectedEpisode);
    }

    // -------------------------------------------------------------------------
    // Species
    // -------------------------------------------------------------------------

    @Feature("Species")
    @Story("Parametrized")
    @ParameterizedTest(name = "species {0} is {1}")
    @CsvSource({
            "1, Human",
            "2, Droid",
            "3, Wookie",
            "5, Hutt",
            "6, Yoda's species"
    })
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get species by ID returns expected name")
    void testGetSpeciesById(int speciesId, String expectedName) {
        var species = swapiClient.getSpecies(speciesId);
        assertEquals(expectedName, species.name,
                "Species " + speciesId + " should be '" + expectedName + "'");
    }

    // -------------------------------------------------------------------------
    // Vehicles
    // -------------------------------------------------------------------------

    @Feature("Vehicles")
    @Story("Parametrized")
    @ParameterizedTest(name = "vehicle {0} is {1}")
    @CsvSource({
            "4, Sand Crawler",
            "6, T-16 skyhopper",
            "7, X-34 landspeeder",
            "8, TIE/LN starfighter",
            "14, Snowspeeder"
    })
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get vehicle by ID returns expected name")
    void testGetVehicleById(int vehicleId, String expectedName) {
        var vehicle = swapiClient.getVehicle(vehicleId);
        assertEquals(expectedName, vehicle.name,
                "Vehicle " + vehicleId + " should be '" + expectedName + "'");
    }

    // -------------------------------------------------------------------------
    // Starships
    // -------------------------------------------------------------------------

    @Feature("Starships")
    @Story("Parametrized")
    @ParameterizedTest(name = "starship {0} is {1}")
    @CsvSource({
            "2, CR90 corvette",
            "3, Star Destroyer",
            "9, Death Star",
            "10, Millennium Falcon",
            "12, X-wing"
    })
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get starship by ID returns expected name")
    void testGetStarshipById(int starshipId, String expectedName) {
        var starship = swapiClient.getStarship(starshipId);
        assertEquals(expectedName, starship.name,
                "Starship " + starshipId + " should be '" + expectedName + "'");
    }

    // -------------------------------------------------------------------------
    // Cross-resource chaining
    // -------------------------------------------------------------------------

    @Feature("Cross-Resource")
    @Story("Resource Chaining")
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Person homeworld URL resolves to a valid planet")
    void testPersonHomeworldResolves() {
        var person = swapiClient.getPerson(1);
        String homeworld = person.homeworld;
        String stripped = homeworld.endsWith("/") ? homeworld.substring(0, homeworld.length() - 1) : homeworld;
        int planetId = Integer.parseInt(stripped.substring(stripped.lastIndexOf('/') + 1));
        var planet = swapiClient.getPlanet(planetId);
        assertEquals("Tatooine", planet.name, "Luke Skywalker's homeworld should be Tatooine");
    }

    @Feature("Cross-Resource")
    @Story("Resource Chaining")
    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Film characters resolve to valid people")
    void testFilmCharactersResolve() {
        var film = swapiClient.getFilm(1);
        String charUrl = film.characters.get(0);
        String stripped = charUrl.endsWith("/") ? charUrl.substring(0, charUrl.length() - 1) : charUrl;
        int charId = Integer.parseInt(stripped.substring(stripped.lastIndexOf('/') + 1));
        var person = swapiClient.getPerson(charId);
        assertNotNull(person.name, "Character should have a name");
        assertFalse(person.name.isBlank(), "Character name should not be blank");
    }

    @Feature("Cross-Resource")
    @Story("Resource Chaining")
    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Planet residents are valid people")
    void testPlanetResidentsResolve() {
        var planet = swapiClient.getPlanet(1);
        String residentUrl = planet.residents.get(0);
        String stripped = residentUrl.endsWith("/")
                ? residentUrl.substring(0, residentUrl.length() - 1) : residentUrl;
        int residentId = Integer.parseInt(stripped.substring(stripped.lastIndexOf('/') + 1));
        var person = swapiClient.getPerson(residentId);
        assertEquals("Luke Skywalker", person.name, "First resident of Tatooine should be Luke Skywalker");
    }

    @Feature("Cross-Resource")
    @Story("Resource Chaining")
    @Feature("Cross-Resource")
    @Story("Resource Chaining")
    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Species people are valid persons")
    void testSpeciesPeopleResolve() {
        var species = swapiClient.getSpecies(1);
        String personUrl = species.people.get(0);
        String stripped = personUrl.endsWith("/")
                ? personUrl.substring(0, personUrl.length() - 1) : personUrl;
        int personId = Integer.parseInt(stripped.substring(stripped.lastIndexOf('/') + 1));
        var person = swapiClient.getPerson(personId);
        assertNotNull(person.name, "Species member should have a name");
        assertFalse(person.name.isBlank(), "Species member name should not be blank");
    }
}
