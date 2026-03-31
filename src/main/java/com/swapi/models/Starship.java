package com.swapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A starship within the Star Wars universe.
 *
 * <p>Example – Death Star (id=9):
 * <ul>
 *   <li>name: "Death Star"</li>
 *   <li>model: "DS-1 Orbital Battle Station"</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Starship {

    @JsonProperty("name")
    public String name;

    @JsonProperty("model")
    public String model;

    @JsonProperty("manufacturer")
    public String manufacturer;

    @JsonProperty("cost_in_credits")
    public String costInCredits;

    @JsonProperty("length")
    public String length;

    @JsonProperty("max_atmosphering_speed")
    public String maxAtmospheringSpeed;

    @JsonProperty("crew")
    public String crew;

    @JsonProperty("passengers")
    public String passengers;

    @JsonProperty("cargo_capacity")
    public String cargoCapacity;

    @JsonProperty("consumables")
    public String consumables;

    @JsonProperty("hyperdrive_rating")
    public String hyperdriveRating;

    @JsonProperty("MGLT")
    public String mglt;

    @JsonProperty("starship_class")
    public String starshipClass;

    @JsonProperty("pilots")
    public List<String> pilots;

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
