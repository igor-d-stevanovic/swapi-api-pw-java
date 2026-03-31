package com.swapi.config;

/**
 * Application settings for the SWAPI test framework.
 *
 * <p>Settings can be overridden via environment variables or Java system properties.
 * Environment variable names use the prefix {@code SWAPI_} (e.g. {@code SWAPI_BASE_URL}).
 * System property names use the same prefix (e.g. {@code -DSWAPI_BASE_URL=...}).
 *
 * <p>Defaults mirror the Python reference implementation.
 */
public class Settings {

    private static final Settings INSTANCE = new Settings();

    /** Base URL of the SWAPI service. */
    public final String baseUrl;

    /** Request timeout in milliseconds. */
    public final int timeoutMs;

    /** Maximum acceptable response time in milliseconds (SLA). */
    public final int maxResponseTimeMs;

    /** Number of retry attempts for transient failures. */
    public final int retries;

    /** Base delay in milliseconds between retries (doubles each attempt). */
    public final long retryDelayMs;

    private Settings() {
        this.baseUrl = get("SWAPI_BASE_URL", "https://swapi.dev");
        this.timeoutMs = getInt("SWAPI_TIMEOUT", 30_000);
        this.maxResponseTimeMs = getInt("SWAPI_MAX_RESPONSE_TIME_MS", 5_000);
        this.retries = getInt("SWAPI_RETRIES", 2);
        this.retryDelayMs = getLong("SWAPI_RETRY_DELAY_MS", 500L);
    }

    /** Returns the singleton Settings instance. */
    public static Settings getInstance() {
        return INSTANCE;
    }

    private static String get(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    private static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
