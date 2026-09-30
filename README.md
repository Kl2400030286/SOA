# ParkGrid Services

ParkGrid is a Maven multi-module Spring Boot 4.0.0 application. The business behavior and
database schema remain compatible with the original consolidated API, while deployable
service boundaries are now explicit:

| Module | Application | Port | Responsibility |
|---|---|---:|---|
| `eureka` | `eureka-server` | 8761 | Service registry |
| `gateway` | `api-gateway` | 8081 | Edge routing and load balancing |
| `services/parking-service` | `parking-service` | 8080 | MySQL-backed auth, zones, slots, and compatibility API |
| `services/booking-service` | `booking-service` | 8082 | Booking facade; forwards authenticated requests to parking |
| `services/billing-service` | `billing-service` | 8083 | Billing facade; obtains quotes from parking |

Booking calls Billing for `/api/bookings/{id}/quote`, and Billing calls the parking service's
internal quote endpoint. The parking service remains the source of truth for transactions,
JWT role enforcement, overlap locking, tariffs, cancellation, and MySQL persistence. This
keeps existing clients working while allowing each service to be deployed independently.

## Build and run

The root `pom.xml` is an aggregator and pins Spring Boot `4.0.0` and Spring Cloud
`2025.1.0`. Run the complete build with:

```text
mvn clean verify
```

Create `parkingslot` in MySQL 8, copy `.env.example` to `.env`, and set `MYSQL_PASSWORD`
and a 32+ character `JWT_SECRET`. Then run:

```text
docker compose up --build
```

Use `http://localhost:8081/api/...` through the gateway. Eureka registration is enabled
by default and can be disabled per service with `EUREKA_ENABLED`, `EUREKA_REGISTER`, or
`EUREKA_FETCH`. Direct service URLs are useful for diagnostics.

Existing endpoints remain under `/api/auth`, `/api/parking`, `/api/bookings`,
`/api/billing`, and `/api/admin`. Zone rates are integer cents from 5 through 100.
