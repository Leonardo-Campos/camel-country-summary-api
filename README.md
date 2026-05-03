# Camel Country Summary API

A Java microservice built with **Spring Boot** and **Apache Camel** that orchestrates multiple public APIs to provide an intelligent country summary.

## Architecture

This microservice demonstrates key Enterprise Integration Patterns (EIPs) using Apache Camel:

```
GET /country-summary?name={COUNTRY_NAME}
         │
         ▼
 ┌─────────────────┐
 │  REST Countries  │  ← Circuit Breaker (Resilience4j)
 │     API Call     │
 └────────┬────────┘
          │
          ▼
 ┌─────────────────┐
 │  Content-Based   │  ← Routes based on country found/not found
 │     Router       │
 └────────┬────────┘
          │
     Found? ──── No ──→ Custom Error Response (404/503)
          │
          Yes
          │
          ▼
 ┌─────────────────┐
 │    Multicast     │  ← Parallel processing
 │   (Parallel)     │
 └───┬─────────┬───┘
     │         │
     ▼         ▼
 ┌───────┐ ┌──────────┐
 │Weather│ │Exchange  │   ← Each with Circuit Breaker
 │  API  │ │Rate API  │
 └───┬───┘ └────┬─────┘
     │          │
     ▼          ▼
 ┌─────────────────┐
 │   Aggregation   │  ← Custom AggregationStrategy
 │    Strategy      │
 └────────┬────────┘
          │
          ▼
    JSON Response
```

## Features

- **REST DSL Endpoint**: `GET /api/country-summary?name={COUNTRY_NAME}`
- **Multicast Pattern**: Parallel calls to OpenWeatherMap and ExchangeRate APIs
- **Aggregation Strategy**: Combines responses from multiple APIs into a single JSON
- **Content-Based Router**: Handles country-not-found scenarios with customized errors
- **Circuit Breaker** (Resilience4j): Protects against external API failures
- **Retry/Timeout**: Handles network instability with configurable timeouts
- **Graceful Degradation**: Returns partial data when individual APIs fail

## APIs Used

| API | Purpose | URL |
|-----|---------|-----|
| REST Countries | Country info (capital, currency, coordinates) | https://restcountries.com |
| OpenWeatherMap | Weather data for the capital city | https://openweathermap.org/api |
| ExchangeRate | Currency exchange rates | https://api.exchangerate.host |

## Prerequisites

- Java 17+
- Maven 3.8+
- OpenWeatherMap API key ([get one here](https://openweathermap.org/appid))
- ExchangeRate API key ([get one here](https://exchangerate.host))

## Configuration

Set the following environment variables locally:

```bash
export OPENWEATHERMAP_API_KEY=your-openweathermap-api-key
export EXCHANGERATE_API_KEY=your-exchangerate-api-key
```

On Windows PowerShell:

```powershell
$env:OPENWEATHERMAP_API_KEY="your-openweathermap-api-key"
$env:EXCHANGERATE_API_KEY="your-exchangerate-api-key"
```

To persist them for future terminals on Windows:

```powershell
[System.Environment]::SetEnvironmentVariable("OPENWEATHERMAP_API_KEY", "your-openweathermap-api-key", "User")
[System.Environment]::SetEnvironmentVariable("EXCHANGERATE_API_KEY", "your-exchangerate-api-key", "User")
```

Do not commit real keys to the repository. For GitHub, keep the values in repository secrets:

- `OPENWEATHERMAP_API_KEY`
- `EXCHANGERATE_API_KEY`

GitHub setup:

1. Open the repository on GitHub.
2. Go to `Settings > Secrets and variables > Actions`.
3. Create the secrets `OPENWEATHERMAP_API_KEY` and `EXCHANGERATE_API_KEY`.
4. Paste the real values there.

The CI workflow already reads these secrets during build execution.

## Running

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Or run the jar directly
java -jar target/camel-country-summary-1.0.0-SNAPSHOT.jar
```

## Docker

Build the image locally:

```bash
docker build -t camel-country-summary:local .
```

Run the container:

```bash
docker run -p 8080:8080 \
  -e OPENWEATHERMAP_API_KEY=your-openweathermap-api-key \
  -e EXCHANGERATE_API_KEY=your-exchangerate-api-key \
  camel-country-summary:local
```

## Usage

### Get Country Summary

```bash
curl "http://localhost:8080/api/country-summary?name=Brazil"
```

### Example Response

```json
{
  "country": "Brazil",
  "capital": "Brasília",
  "currency": "Brazilian real",
  "currencyCode": "BRL",
  "latitude": -10.0,
  "longitude": -55.0,
  "weather": {
    "description": "scattered clouds",
    "temperature": 28.5,
    "feelsLike": 30.2,
    "humidity": 65,
    "windSpeed": 3.5
  },
  "exchangeRate": {
    "baseCurrency": "BRL",
    "rates": {
      "USD": 0.19,
      "EUR": 0.17,
      "GBP": 0.15,
      "JPY": 28.5
    }
  }
}
```

`exchangeRate.baseCurrency` now uses the country's own `currencyCode` as the base currency when querying the exchange API.

### Error Response (Country Not Found)

```bash
curl "http://localhost:8080/api/country-summary?name=InvalidCountry"
```

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Country 'InvalidCountry' was not found. Please check the spelling and try again."
}
```

## Resilience Patterns

### Circuit Breaker (Resilience4j)
- **Failure Rate Threshold**: 50%
- **Wait Duration in Open State**: 20-30 seconds
- **Sliding Window Size**: 5-10 calls
- Fallback handlers return graceful error messages

### Timeout
- Multicast timeout: 10 seconds
- Individual API calls inherit Camel HTTP timeout defaults

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.5**
- **Apache Camel 4.5.0**
- **Resilience4j** (Circuit Breaker)
- **Jackson** (JSON processing)
- **Maven** (Build tool)

## CI

The repository includes a GitHub Actions workflow that runs on every push and pull request:

- Maven dependency cache
- Java 17 setup
- `mvn clean verify`
- Docker image build with Buildx
- GHCR push on `push`
- Deployment manifest artifact upload

## Deploy Preparation

Deployment starter manifests are available in [deploy/k8s](/C:/Users/lopes/IdeaProjects/camel-country-summary-api/deploy/k8s):

- [deployment.yaml](/C:/Users/lopes/IdeaProjects/camel-country-summary-api/deploy/k8s/deployment.yaml:1)
- [service.yaml](/C:/Users/lopes/IdeaProjects/camel-country-summary-api/deploy/k8s/service.yaml:1)
- [secret-example.yaml](/C:/Users/lopes/IdeaProjects/camel-country-summary-api/deploy/k8s/secret-example.yaml:1)

Before deploying:

1. Replace `ghcr.io/OWNER/camel-country-summary:latest` with your real image path.
2. Create the Kubernetes secret from your real API keys.
3. Apply the manifests to your cluster.
