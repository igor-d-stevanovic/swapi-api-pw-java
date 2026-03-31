package com.swapi.clients;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Base API client wrapping Playwright's {@link APIRequestContext}.
 *
 * <p>Features:
 * <ul>
 *   <li>Request/response <b>logging</b> (method, URL, status, elapsed time).</li>
 *   <li><b>Response timing</b> via {@link TimedResponse} wrapper.</li>
 *   <li><b>Automatic retries</b> with exponential back-off for transient failures.</li>
 * </ul>
 */
public class BaseApiClient {

    private static final Logger log = LoggerFactory.getLogger("swapi.client");

    private static final Set<Integer> DEFAULT_RETRY_STATUSES = Set.of(500, 502, 503, 504, 408, 429);

    protected final APIRequestContext context;
    private final int retries;
    private final long retryDelayMs;
    private final Set<Integer> retryStatuses;

    /**
     * Creates a new client with explicit retry configuration.
     *
     * @param context      Playwright API request context
     * @param retries      number of retry attempts for transient errors
     * @param retryDelayMs base delay in milliseconds between retries (doubles each attempt)
     */
    public BaseApiClient(APIRequestContext context, int retries, long retryDelayMs) {
        this.context = context;
        this.retries = retries;
        this.retryDelayMs = retryDelayMs;
        this.retryStatuses = DEFAULT_RETRY_STATUSES;
    }

    /**
     * Creates a new client with default retry configuration (2 retries, 500 ms base delay).
     *
     * @param context Playwright API request context
     */
    public BaseApiClient(APIRequestContext context) {
        this(context, 2, 500L);
    }

    // -------------------------------------------------------------------------
    // Public helpers
    // -------------------------------------------------------------------------

    /**
     * Sends a GET request and returns the raw {@link APIResponse}.
     * Retries automatically on transient HTTP errors.
     */
    public APIResponse get(String endpoint) {
        return requestWithRetry(endpoint, null).response();
    }

    /**
     * Sends a GET request with query parameters and returns the raw {@link APIResponse}.
     */
    public APIResponse get(String endpoint, RequestOptions options) {
        return requestWithRetry(endpoint, options).response();
    }

    /**
     * Sends a GET request and returns a {@link TimedResponse} with elapsed wall-clock time.
     * Use this when you need to assert on response latency.
     */
    public TimedResponse getTimed(String endpoint) {
        return requestWithRetry(endpoint, null);
    }

    /**
     * Sends a GET request with query parameters and returns a {@link TimedResponse}.
     */
    public TimedResponse getTimed(String endpoint, RequestOptions options) {
        return requestWithRetry(endpoint, options);
    }

    /**
     * Sends a GET request and returns only the HTTP status code.
     */
    public int getStatus(String endpoint) {
        return get(endpoint).status();
    }

    // -------------------------------------------------------------------------
    // Internal retry engine
    // -------------------------------------------------------------------------

    private TimedResponse requestWithRetry(String endpoint, RequestOptions options) {
        TimedResponse last = null;
        long delay = retryDelayMs;

        for (int attempt = 0; attempt <= retries; attempt++) {
            long start = System.nanoTime();
            APIResponse response = (options != null)
                    ? context.get(endpoint, options)
                    : context.get(endpoint);
            double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;

            last = new TimedResponse(response, elapsedMs);

            log.info("GET {} -> {} ({:.0f} ms){}",
                    endpoint,
                    response.status(),
                    elapsedMs,
                    attempt > 0 ? " [attempt " + (attempt + 1) + "/" + (retries + 1) + "]" : "");

            if (!retryStatuses.contains(response.status())) {
                return last;
            }

            if (attempt < retries) {
                log.warn("Retryable status {} for {} — retrying in {} ms", response.status(), endpoint, delay);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                delay *= 2;
            }
        }

        log.error("All {} retries exhausted for {} (last status: {})", retries, endpoint, last.status());
        return last;
    }
}
