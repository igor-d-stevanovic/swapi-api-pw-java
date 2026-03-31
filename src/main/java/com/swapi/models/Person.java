package com.swapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A person within the Star Wars universe.
 *
 * <p>Example – Luke Skywalker (id=1):
 * <ul>
 *   <li>name: "Luke Skywalker"</li>
 *   <li>birthYear: "19BBY"</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {

    @JsonProperty("name")
    public String name;

    @JsonProperty("height")
    public String height;

    @JsonProperty("mass")
    public String mass;

    @JsonProperty("hair_color")
    public String hairColor;

    @JsonProperty("skin_color")
    public String skinColor;

    @JsonProperty("eye_color")
    public String eyeColor;

    @JsonProperty("birth_year")
    public String birthYear;

    @JsonProperty("gender")
    public String gender;

    @JsonProperty("homeworld")
    public String homeworld;

    @JsonProperty("films")
    public List<String> films;

    @JsonProperty("species")
    public List<String> species;

    @JsonProperty("vehicles")
    public List<String> vehicles;

    @JsonProperty("starships")
    public List<String> starships;

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
