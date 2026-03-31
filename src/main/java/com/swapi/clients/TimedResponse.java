package com.swapi.clients;

import com.microsoft.playwright.APIResponse;

/**
 * Wraps a Playwright {@link APIResponse} with elapsed-time metadata.
 *
 * <p>Convenience accessors delegate to the underlying response so callers
 * don't always need {@code .response()}.
 */
public class TimedResponse {

    private final APIResponse response;
    private final double elapsedMs;

    /**
     * Creates a new {@code TimedResponse}.
     *
     * @param response  the original Playwright response
     * @param elapsedMs wall-clock time in milliseconds for the request
     */
    public TimedResponse(APIResponse response, double elapsedMs) {
        this.response = response;
        this.elapsedMs = elapsedMs;
    }

    /** Returns the original Playwright {@link APIResponse}. */
    public APIResponse response() {
        return response;
    }

    /** Returns the elapsed wall-clock time in milliseconds. */
    public double elapsedMs() {
        return elapsedMs;
    }

    /** Convenience proxy: returns the HTTP status code. */
    public int status() {
        return response.status();
    }

    /** Convenience proxy: returns the request URL. */
    public String url() {
        return response.url();
    }
}
