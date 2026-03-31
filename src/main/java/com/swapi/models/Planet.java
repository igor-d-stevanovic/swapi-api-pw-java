package com.swapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A planet within the Star Wars universe.
 *
 * <p>Example – Tatooine (id=1):
 * <ul>
 *   <li>name: "Tatooine"</li>
 *   <li>climate: "arid"</li>
 *   <li>terrain: "desert"</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Planet {

    @JsonProperty("name")
    public String name;

    @JsonProperty("rotation_period")
    public String rotationPeriod;

    @JsonProperty("orbital_period")
    public String orbitalPeriod;

    @JsonProperty("diameter")
    public String diameter;

    @JsonProperty("climate")
    public String climate;

    @JsonProperty("gravity")
    public String gravity;

    @JsonProperty("terrain")
    public String terrain;

    @JsonProperty("surface_water")
    public String surfaceWater;

    @JsonProperty("population")
    public String population;

    @JsonProperty("residents")
    public List<String> residents;

    @JsonProperty("films")
    public List<String> films;

    @JsonProperty("created")
    public String created;

    @JsonProperty("edited")
    public String edited;

    @JsonProperty("url")
    public String url;

    /** Extracts the numeric ID from the resource URL. */
    public int getResourceId() {
        String stripped = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        return Integer.parseInt(stripped.substring(stripped.lastIndexOf('/') + 1));
    }
}
