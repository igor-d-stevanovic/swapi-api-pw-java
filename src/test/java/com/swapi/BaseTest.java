package com.swapi;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.swapi.clients.SwapiClient;
import com.swapi.config.Settings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all SWAPI API tests.
 *
 * <p>Manages a session-scoped Playwright instance and {@link SwapiClient}
 * so that the expensive browser setup happens only once per test class.
 *
 * <p>Subclasses can access:
 * <ul>
 *   <li>{@link #swapiClient} – for typed, model-backed API calls</li>
 *   <li>{@link #apiContext} – for raw Playwright API calls</li>
 * </ul>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    protected static final Settings SETTINGS = Settings.getInstance();

    protected Playwright playwright;
    protected APIRequestContext apiContext;
    protected SwapiClient swapiClient;

    @BeforeAll
    void setUpAll() {
        playwright = Playwright.create();

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");

        apiContext = playwright.request().newContext(
                new com.microsoft.playwright.APIRequest.NewContextOptions()
                        .setBaseURL(SETTINGS.baseUrl)
                        .setExtraHTTPHeaders(headers)
                        .setTimeout(SETTINGS.timeoutMs));

        swapiClient = new SwapiClient(apiContext);
    }

    @AfterAll
    void tearDownAll() {
        if (apiContext != null) {
            apiContext.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
