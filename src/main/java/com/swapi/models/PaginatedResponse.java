package com.swapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Generic paginated response returned by all SWAPI list endpoints.
 *
 * @param <T> the type of resource objects on each page
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginatedResponse<T> {

    /** Total number of resources matching the query. */
    @JsonProperty("count")
    public int count;

    /** URL of the next page, or {@code null} if this is the last page. */
    @JsonProperty("next")
    public String next;

    /** URL of the previous page, or {@code null} if this is the first page. */
    @JsonProperty("previous")
    public String previous;

    /** List of resource objects on the current page. */
    @JsonProperty("results")
    public List<T> results;
}
