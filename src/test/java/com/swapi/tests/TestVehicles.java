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
 * Tests for the SWAPI Vehicles resource: GET /api/vehicles/
 *
 * <p>Covers: list all, get by ID, search, pagination, not found, schema validation.
 */
@Epic("SWAPI")
@Feature("Vehicles")
public class TestVehicles extends BaseTest {

    // -------------------------------------------------------------------------
    // List Vehicles
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("List Vehicles")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get all vehicles returns paginated results")
    void testGetAllVehicles() {
        var data = swapiClient.getAllVehicles();
        AssertionUtils.assertValidPagination(data);
    }

    @Test
    @Story("List Vehicles")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Vehicles page size is at most 10")
    void testVehiclesPageSize() {
        var data = swapiClient.getAllVehicles();
        assertTrue(data.results.size() <= 10, "Default page should have at most 10 results");
    }

    @Test
    @Story("List Vehicles")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Vehicles pagination returns different results per page")
    void testVehiclesPagination() {
        var page1 = swapiClient.getAllVehicles(1, null);
        var page2 = swapiClient.getAllVehicles(2, null);

        assertNotNull(page2.previous, "Page 2 should have a 'previous' link");
        Set<String> page1Names = page1.results.stream()
                .map(v -> v.name).collect(Collectors.toSet());
        Set<String> page2Names = page2.results.stream()
                .map(v -> v.name).collect(Collectors.toSet());
        assertNotEquals(page1Names, page2Names, "Page 1 and 2 should have different vehicles");
    }

    @Test
    @Story("List Vehicles")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Search vehicles by name returns matching results")
    void testSearchVehicles() {
        var data = swapiClient.getAllVehicles(null, "sand");
        assertTrue(data.count >= 1, "Expected at least 1 result for search 'sand'");
        AssertionUtils.assertSearchResultsContain(data, v -> v.name, "sand", "name");
    }

    @Test
    @Story("List Vehicles")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Search vehicles with nonsense term returns empty")
    void testSearchNoResults() {
        var data = swapiClient.getAllVehicles(null, "xyznonexistent");
        assertEquals(0, data.count, "Expected zero results for nonsense search");
    }

    // -------------------------------------------------------------------------
    // Get Vehicle by ID
    // -------------------------------------------------------------------------

    @Test
    @Tag("smoke")
    @Story("Get Vehicle by ID")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Get vehicle by ID returns Sand Crawler")
    void testGetVehicleById() {
        var vehicle = swapiClient.getVehicle(4);
        assertEquals("Sand Crawler", vehicle.name);
        assertEquals("Digger Crawler", vehicle.model);
    }

    @Test
    @Story("Get Vehicle by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Vehicle has correct manufacturer")
    void testVehicleManufacturer() {
        var vehicle = swapiClient.getVehicle(4);
        assertTrue(vehicle.manufacturer.contains("Corellia Mining Corporation"),
                "Sand Crawler should be manufactured by Corellia Mining Corporation");
    }

    @Test
    @Story("Get Vehicle by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Vehicle appears in at least one film")
    void testVehicleHasFilms() {
        var vehicle = swapiClient.getVehicle(4);
        assertFalse(vehicle.films.isEmpty(), "Sand Crawler should appear in at least one film");
    }

    @Test
    @Story("Get Vehicle by ID")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Vehicle response passes schema validation")
    void testVehicleSchemaValidation() {
        var vehicle = swapiClient.getVehicle(4);
        assertNotNull(vehicle.name, "name should not be null");
        assertFalse(vehicle.name.isBlank(), "name should not be blank");
        assertTrue(vehicle.url.endsWith("/4/"), "url should end with /4/");
    }

    @Test
    @Story("Get Vehicle by ID")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Non-existent vehicle returns 404")
    void testVehicleNotFound() {
        var response = swapiClient.getVehicleRaw(9999);
        AssertionUtils.assertStatusNotFound(response);
    }
}
