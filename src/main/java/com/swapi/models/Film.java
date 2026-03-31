package com.swapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A Star Wars film.
 *
 * <p>Example – A New Hope (id=1):
 * <ul>
 *   <li>title: "A New Hope"</li>
 *   <li>episodeId: 4</li>
 *   <li>director: "George Lucas"</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Film {

    @JsonProperty("title")
    public String title;

    @JsonProperty("episode_id")
    public int episodeId;

    @JsonProperty("opening_crawl")
    public String openingCrawl;

    @JsonProperty("director")
    public String director;

    @JsonProperty("producer")
    public String producer;

    @JsonProperty("release_date")
    public String releaseDate;

    @JsonProperty("characters")
    public List<String> characters;

    @JsonProperty("planets")
    public List<String> planets;

    @JsonProperty("starships")
    public List<String> starships;

    @JsonProperty("vehicles")
    public List<String> vehicles;

    @JsonProperty("species")
    public List<String> species;

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
