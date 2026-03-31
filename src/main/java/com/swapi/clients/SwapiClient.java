package com.swapi.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.swapi.config.Settings;
import com.swapi.models.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;

/**
 * Typed API client for <a href="https://swapi.dev/api/">https://swapi.dev/api/</a>.
 *
 * <p>Each method fetches raw JSON, deserializes it with Jackson, and returns a
 * fully typed Java object.  This gives you IDE auto-complete and automatic
 * schema validation in every test.
 */
public class SwapiClient extends BaseApiClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public SwapiClient(APIRequestContext context) {
        super(context,
                Settings.getInstance().retries,
                Settings.getInstance().retryDelayMs);
    }

    // -------------------------------------------------------------------------
    // Root
    // -------------------------------------------------------------------------

    /** GET /api/ — returns a mapping of resource name → URL. */
    public Map<String, String> getRoot() {
        return getJson("/api/", new TypeReference<Map<String, String>>() {});
    }

    // -------------------------------------------------------------------------
    // People
    // -------------------------------------------------------------------------

    /** GET /api/people/ with optional pagination and search. */
    public PaginatedResponse<Person> getAllPeople(Integer page, String search) {
        APIResponse response = get("/api/people/", buildOptions(page, search));
        return parsePagedResponse(response, new TypeReference<PaginatedResponse<Person>>() {});
    }

    /** GET /api/people/ (no parameters). */
    public PaginatedResponse<Person> getAllPeople() {
        return getAllPeople(null, null);
    }

    /** GET /api/people/{id}/ */
    public Person getPerson(int personId) {
        return getJson("/api/people/" + personId + "/", new TypeReference<Person>() {});
    }

    /** GET /api/people/{id}/ — raw response for status assertions. */
    public APIResponse getPersonRaw(int personId) {
        return get("/api/people/" + personId + "/");
    }

    // -------------------------------------------------------------------------
    // Planets
    // -------------------------------------------------------------------------

    /** GET /api/planets/ with optional pagination and search. */
    public PaginatedResponse<Planet> getAllPlanets(Integer page, String search) {
        APIResponse response = get("/api/planets/", buildOptions(page, search));
        return parsePagedResponse(response, new TypeReference<PaginatedResponse<Planet>>() {});
    }

    /** GET /api/planets/ (no parameters). */
    public PaginatedResponse<Planet> getAllPlanets() {
        return getAllPlanets(null, null);
    }

    /** GET /api/planets/{id}/ */
    public Planet getPlanet(int planetId) {
        return getJson("/api/planets/" + planetId + "/", new TypeReference<Planet>() {});
    }

    /** GET /api/planets/{id}/ — raw response for status assertions. */
    public APIResponse getPlanetRaw(int planetId) {
        return get("/api/planets/" + planetId + "/");
    }

    // -------------------------------------------------------------------------
    // Films
    // -------------------------------------------------------------------------

    /** GET /api/films/ with optional pagination and search. */
    public PaginatedResponse<Film> getAllFilms(Integer page, String search) {
        APIResponse response = get("/api/films/", buildOptions(page, search));
        return parsePagedResponse(response, new TypeReference<PaginatedResponse<Film>>() {});
    }

    /** GET /api/films/ (no parameters). */
    public PaginatedResponse<Film> getAllFilms() {
        return getAllFilms(null, null);
    }

    /** GET /api/films/{id}/ */
    public Film getFilm(int filmId) {
        return getJson("/api/films/" + filmId + "/", new TypeReference<Film>() {});
    }

    /** GET /api/films/{id}/ — raw response for status assertions. */
    public APIResponse getFilmRaw(int filmId) {
        return get("/api/films/" + filmId + "/");
    }

    // -------------------------------------------------------------------------
    // Species
    // -------------------------------------------------------------------------

    /** GET /api/species/ with optional pagination and search. */
    public PaginatedResponse<Species> getAllSpecies(Integer page, String search) {
        APIResponse response = get("/api/species/", buildOptions(page, search));
        return parsePagedResponse(response, new TypeReference<PaginatedResponse<Species>>() {});
    }

    /** GET /api/species/ (no parameters). */
    public PaginatedResponse<Species> getAllSpecies() {
        return getAllSpecies(null, null);
    }

    /** GET /api/species/{id}/ */
    public Species getSpecies(int speciesId) {
        return getJson("/api/species/" + speciesId + "/", new TypeReference<Species>() {});
    }

    /** GET /api/species/{id}/ — raw response for status assertions. */
    public APIResponse getSpeciesRaw(int speciesId) {
        return get("/api/species/" + speciesId + "/");
    }

    // -------------------------------------------------------------------------
    // Vehicles
    // -------------------------------------------------------------------------

    /** GET /api/vehicles/ with optional pagination and search. */
    public PaginatedResponse<Vehicle> getAllVehicles(Integer page, String search) {
        APIResponse response = get("/api/vehicles/", buildOptions(page, search));
        return parsePagedResponse(response, new TypeReference<PaginatedResponse<Vehicle>>() {});
    }

    /** GET /api/vehicles/ (no parameters). */
    public PaginatedResponse<Vehicle> getAllVehicles() {
        return getAllVehicles(null, null);
    }

    /** GET /api/vehicles/{id}/ */
    public Vehicle getVehicle(int vehicleId) {
        return getJson("/api/vehicles/" + vehicleId + "/", new TypeReference<Vehicle>() {});
    }

    /** GET /api/vehicles/{id}/ — raw response for status assertions. */
    public APIResponse getVehicleRaw(int vehicleId) {
        return get("/api/vehicles/" + vehicleId + "/");
    }

    // -------------------------------------------------------------------------
    // Starships
    // -------------------------------------------------------------------------

    /** GET /api/starships/ with optional pagination and search. */
    public PaginatedResponse<Starship> getAllStarships(Integer page, String search) {
        APIResponse response = get("/api/starships/", buildOptions(page, search));
        return parsePagedResponse(response, new TypeReference<PaginatedResponse<Starship>>() {});
    }

    /** GET /api/starships/ (no parameters). */
    public PaginatedResponse<Starship> getAllStarships() {
        return getAllStarships(null, null);
    }

    /** GET /api/starships/{id}/ */
    public Starship getStarship(int starshipId) {
        return getJson("/api/starships/" + starshipId + "/", new TypeReference<Starship>() {});
    }

    /** GET /api/starships/{id}/ — raw response for status assertions. */
    public APIResponse getStarshipRaw(int starshipId) {
        return get("/api/starships/" + starshipId + "/");
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private RequestOptions buildOptions(Integer page, String search) {
        if (page == null && search == null) {
            return null;
        }
        RequestOptions options = RequestOptions.create();
        if (page != null) {
            options.setQueryParam("page", page);
        }
        if (search != null) {
            options.setQueryParam("search", search);
        }
        return options;
    }

    private <T> T getJson(String endpoint, TypeReference<T> type) {
        APIResponse response = get(endpoint);
        return deserialize(response, type);
    }

    private <T> T parsePagedResponse(APIResponse response, TypeReference<T> type) {
        return deserialize(response, type);
    }

    private <T> T deserialize(APIResponse response, TypeReference<T> type) {
        try {
            return MAPPER.readValue(response.body(), type);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Failed to deserialize response from " + response.url(), e);
        }
    }
}
