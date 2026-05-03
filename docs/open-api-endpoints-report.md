# Open API Endpoints Report

Analysis of external API endpoints used in **camel-country-summary-api** — identifying which require API keys and which are open (no authentication needed).

---

## Current Endpoints

| API | Base URL | Auth Required | Method | Used In |
|-----|----------|---------------|--------|---------|
| REST Countries | `https://restcountries.com/v3.1` | **No** | `GET /name/{name}?fields=name,capital,currencies,latlng` | `CountryInfoRoute` |
| OpenWeatherMap | `https://api.openweathermap.org/data/2.5` | **Yes** (`appid` query param) | `GET /weather?lat={lat}&lon={lon}&appid={key}&units=metric` | `WeatherRoute` |
| ExchangeRate API | `https://api.exchangerate.host` | **Yes** (`access_key` query param) | `GET /latest?base={code}&symbols=USD,EUR,GBP,JPY,BRL&access_key={key}` | `ExchangeRateRoute` |

---

## Open Endpoints (No API Key Required)

### 1. REST Countries API
- **URL**: `https://restcountries.com/v3.1/name/{name}?fields=name,capital,currencies,latlng`
- **Method**: `GET`
- **Auth**: None
- **Rate Limit**: No documented rate limit
- **Status**: Currently used in the project — fully open

---

## Restricted Endpoints (API Key Required)

### 2. OpenWeatherMap API
- **URL**: `https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={key}&units=metric`
- **Method**: `GET`
- **Auth**: `appid` query parameter (free tier available with signup)
- **Free Tier**: 60 calls/minute, 1,000,000 calls/month

### 3. ExchangeRate API (exchangerate.host)
- **URL**: `https://api.exchangerate.host/latest?base={code}&symbols=USD,EUR,GBP,JPY,BRL&access_key={key}`
- **Method**: `GET`
- **Auth**: `access_key` query parameter
- **Free Tier**: Limited requests, requires signup

---

## Suggested Open Alternatives (No API Key Required)

### Weather: Open-Meteo
- **URL**: `https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code,apparent_temperature`
- **Method**: `GET`
- **Auth**: None
- **Rate Limit**: Non-commercial use is free
- **Documentation**: https://open-meteo.com/en/docs
- **Response format**: JSON with `current.temperature_2m`, `current.relative_humidity_2m`, `current.wind_speed_10m`, `current.apparent_temperature`, `current.weather_code`

### Exchange Rates: Frankfurter
- **URL**: `https://api.frankfurter.dev/v2/rates?base={code}&quotes=USD,EUR,GBP,JPY,BRL`
- **Method**: `GET`
- **Auth**: None
- **Rate Limit**: No documented limit (daily updates)
- **Documentation**: https://frankfurter.dev/
- **Response format**: JSON with `base` and `rates` object

### Exchange Rates: ExchangeRate-API (Open Access)
- **URL**: `https://open.er-api.com/v6/latest/{code}`
- **Method**: `GET`
- **Auth**: None (attribution required)
- **Rate Limit**: Rate limited (once per 24h recommended)
- **Documentation**: https://www.exchangerate-api.com/docs/free

---

## Summary

| Endpoint | Auth | Open Alternative |
|----------|------|------------------|
| REST Countries (`restcountries.com`) | **Open** | — (already open) |
| OpenWeatherMap (`openweathermap.org`) | **Key required** | Open-Meteo (`open-meteo.com`) — no key |
| ExchangeRate (`exchangerate.host`) | **Key required** | Frankfurter (`frankfurter.dev`) — no key |
