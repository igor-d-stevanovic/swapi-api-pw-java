package com.swapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A species within the Star Wars universe.
 *
 * <p>Example – Human (id=1):
 * <ul>
 *   <li>name: "Human"</li>
 *   <li>classification: "mammal"</li>
 *   <li>designation: "sentient"</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Species {

    @JsonProperty("name")
    public String name;

    @JsonProperty("classification")
    public String classification;

    @JsonProperty("designation")
    public String designation;

    @JsonProperty("average_height")
    public String averageHeight;

    @JsonProperty("skin_colors")
    public String skinColors;

    @JsonProperty("hair_colors")
    public String hairColors;

    @JsonProperty("eye_colors")
    public String eyeColors;

    @JsonProperty("average_lifespan")
    public String averageLifespan;

    @JsonProperty("homeworld")
    public String homeworld;

    @JsonProperty("language")
    public String language;

    @JsonProperty("people")
    public List<String> people;

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
