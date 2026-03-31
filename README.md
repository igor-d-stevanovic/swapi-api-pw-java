# swapi-api-pw-java

A comprehensive API test suite for the [Star Wars API (SWAPI)](https://swapi.dev), built with **Playwright Java**, **JUnit 5**, and **Allure** reporting.

[![CI](https://github.com/igor-d-stevanovic/swapi-api-pw-java/actions/workflows/ci.yml/badge.svg)](https://github.com/igor-d-stevanovic/swapi-api-pw-java/actions/workflows/ci.yml)

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running Tests](#running-tests)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Test Coverage](#test-coverage)
- [Allure Reporting](#allure-reporting)
- [CI/CD](#cicd)

---

## Overview

This project validates all publicly available endpoints of the [SWAPI REST API](https://swapi.dev/api/) using a typed Java client built on top of Playwright's `APIRequestContext`. It demonstrates:

- Strongly-typed models for every SWAPI resource (People, Planets, Films, Species, Vehicles, Starships)
- Automatic request retries with exponential back-off for transient failures
- Response-time SLA assertions
- Parametrized and data-driven tests
- Cross-resource navigation (e.g., resolve a character's homeworld via a nested URL)
- Rich HTML test reports published to GitHub Pages via Allure

---

## Tech Stack

| Component | Library / Tool | Version |
|-----------|---------------|---------|
| Language | Java | 17 |
| HTTP client | [Playwright Java](https://playwright.dev/java/) | 1.49.0 |
| Test framework | [JUnit 5 (Jupiter)](https://junit.org/junit5/) | 5.10.2 |
| JSON deserialisation | [Jackson Databind](https://github.com/FasterXML/jackson) | 2.17.0 |
| Test reporting | [Allure Framework](https://allurereport.org/) | 2.27.0 |
| Logging | SLF4J Simple | 2.0.12 |
| Build tool | Maven | 3.6+ |

---

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- Internet access to reach `https://swapi.dev`

---

## Installation & Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/igor-d-stevanovic/swapi-api-pw-java.git
   cd swapi-api-pw-java
   ```

2. **Install Maven dependencies**

   ```bash
   mvn clean install -DskipTests
   ```

3. **Install Playwright browser binaries** (required by the Playwright Java runtime)

   ```bash
   mvn exec:java -e \
     -D exec.mainClass=com.microsoft.playwright.CLI \
     -D exec.args="install --with-deps" \
     -D exec.classpathScope=test
   ```

---

## Running Tests

### Run all tests

```bash
mvn test
```

### Run a specific test class

```bash
mvn test -Dtest=TestPeople
```

### Run a specific test method

```bash
mvn test -Dtest=TestPeople#testGetAllPeople
```

### Run tests by tag group

```bash
mvn test -Dgroups=smoke
mvn test -Dgroups="smoke | performance"
```

---

## Configuration

All settings can be overridden via **Java system properties** (`-D` flags) or **environment variables**. System properties take precedence over environment variables.

| Variable | Default | Description |
|----------|---------|-------------|
| `SWAPI_BASE_URL` | `https://swapi.dev` | Base URL of the SWAPI service |
| `SWAPI_TIMEOUT` | `30000` | Request timeout in milliseconds |
| `SWAPI_MAX_RESPONSE_TIME_MS` | `5000` | Maximum acceptable response time (SLA) in milliseconds |
| `SWAPI_RETRIES` | `2` | Number of retry attempts for transient HTTP failures |
| `SWAPI_RETRY_DELAY_MS` | `500` | Base delay between retries in milliseconds (doubles each attempt) |

**Example – override via system properties:**

```bash
mvn test \
  -DSWAPI_BASE_URL=https://swapi.dev \
  -DSWAPI_TIMEOUT=60000 \
  -DSWAPI_MAX_RESPONSE_TIME_MS=10000 \
  -DSWAPI_RETRIES=3 \
  -DSWAPI_RETRY_DELAY_MS=1000
```

**Example – override via environment variables:**

```bash
export SWAPI_BASE_URL="https://swapi.dev"
export SWAPI_TIMEOUT="45000"
mvn test
```

---

## Project Structure

```
swapi-api-pw-java/
├── .github/
│   └── workflows/
│       ├── ci.yml          # Main CI pipeline (test + Allure report + Pages deploy)
│       └── static.yml      # Static content deployment to GitHub Pages
├── pom.xml
└── src/
    ├── main/java/com/swapi/
    │   ├── clients/
    │   │   ├── BaseApiClient.java      # HTTP client with retry logic & timing
    │   │   ├── SwapiClient.java        # Typed API client for all SWAPI resources
    │   │   └── TimedResponse.java      # Response wrapper that captures elapsed time
    │   ├── config/
    │   │   └── Settings.java           # Centralised configuration (env vars / system props)
    │   ├── models/
    │   │   ├── Film.java
    │   │   ├── PaginatedResponse.java  # Generic wrapper for paginated list responses
    │   │   ├── Person.java
    │   │   ├── Planet.java
    │   │   ├── Species.java
    │   │   ├── Starship.java
    │   │   └── Vehicle.java
    │   └── utils/
    │       └── AssertionUtils.java     # Reusable assertion helpers
    └── test/java/com/swapi/
        ├── BaseTest.java               # Session-scoped Playwright setup / teardown
        └── tests/
            ├── TestFilms.java
            ├── TestNegative.java       # Negative / edge-case tests
            ├── TestParametrized.java   # Data-driven parametrized tests
            ├── TestPeople.java
            ├── TestPlanets.java
            ├── TestRoot.java
            ├── TestSpecies.java
            ├── TestStarships.java
            └── TestVehicles.java
```

---

## Test Coverage

| Resource | Endpoint | Scenarios |
|----------|----------|-----------|
| Root | `GET /api/` | Resource listing, schema |
| People | `GET /api/people/`, `GET /api/people/{id}/` | List, get by ID, search, pagination, cross-resource (homeworld), SLA |
| Planets | `GET /api/planets/`, `GET /api/planets/{id}/` | List, get by ID, search, pagination, SLA |
| Films | `GET /api/films/`, `GET /api/films/{id}/` | List, get by ID, schema validation, SLA |
| Species | `GET /api/species/`, `GET /api/species/{id}/` | List, get by ID, search, SLA |
| Vehicles | `GET /api/vehicles/`, `GET /api/vehicles/{id}/` | List, get by ID, search, SLA |
| Starships | `GET /api/starships/`, `GET /api/starships/{id}/` | List, get by ID, search, SLA |
| Negative | All resources | Invalid IDs, malformed parameters, special characters (404 handling) |
| Parametrized | People / Planets / Films | Data-driven tests across multiple IDs and search terms |

---

## Allure Reporting

### Generate and view the report locally

```bash
# Run tests (results are written to target/allure-results/)
mvn test

# Generate the HTML report
mvn allure:report

# Open the report
open target/site/allure-maven-plugin/index.html
```

### Published report

After every push to `main`/`master`, the Allure report is automatically deployed to **GitHub Pages**:

> `https://igor-d-stevanovic.github.io/swapi-api-pw-java/`

---

## CI/CD

The project uses **GitHub Actions** for continuous integration.

### Workflow: `CI` (`.github/workflows/ci.yml`)

| Trigger | Jobs |
|---------|------|
| Push to `main` / `master` | `test` → `deploy` |
| Pull request to `main` / `master` | `test` only |

**`test` job steps:**
1. Check out repository
2. Set up Java 17 (Temurin, Maven cache)
3. Install Playwright browser binaries
4. Run `mvn test`
5. Upload `allure-results` as a build artifact (30-day retention)
6. Generate Allure HTML report (`mvn allure:report`)
7. Upload `allure-report` as a build artifact (30-day retention)
8. Upload report to GitHub Pages artifact (push events only)

**`deploy` job:**
- Deploys the Allure report to GitHub Pages (runs after `test`, even on test failure)
