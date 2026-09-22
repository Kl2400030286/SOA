# ParkGrid Solutions

Runnable Spring Boot implementation of the ParkGrid parking, booking, billing, JWT authentication and admin metrics APIs. The domain remains in one deployable service for now, with Eureka and an API Gateway added as runnable infrastructure around it.

## MySQL 8 setup

Copy `.env.example` to `.env`, set `MYSQL_PASSWORD` to your MySQL root password, and replace `JWT_SECRET` with a private 32+ character value. Credentials are used only through the local `.env` file and are not embedded in Java or Docker configuration. Start the database, Eureka server, gateway and API with `docker compose up --build`. Without Docker, create database `parkingslot` in MySQL 8 and start the main service with `MYSQL_PASSWORD` set in the environment.

## Service URLs

- Eureka dashboard: `http://localhost:8761`
- API Gateway: `http://localhost:8081`
- Consolidated API direct port: `http://localhost:8080`

When Docker Compose is running, use `http://localhost:8081/api/...` for client requests. The gateway discovers `PARKGRID-SERVICE` through Eureka and load-balances requests to the registered application instance. The direct port is retained for local development and diagnostics.

Hibernate creates and updates the tables on first startup. `database/init.sql` creates the database; it does not contain application passwords or seed data.

## API flow

1. `POST /api/auth/register` or `/api/auth/login` with `{"email":"driver@example.com","password":"password123"}`.
2. Send `Authorization: Bearer <token>` to `POST /api/parking/zones` as an admin, for example `{"name":"Central Mall","address":"1 Main Street","capacity":50}`.
3. Read `GET /api/parking/zones/{zoneId}/availability` and `GET /api/parking/zones/{zoneId}/slots`.
4. Reserve with `POST /api/bookings` and `{"slotId":1,"startTime":"2030-01-01T09:00:00Z","endTime":"2030-01-01T11:30:00Z"}`. The pessimistic slot lock and overlap query prevent concurrent double booking.
5. Calculate the charge with `GET /api/billing/bookings/{bookingId}/quote`; admins can view `GET /api/admin/metrics`.

All monetary values are integer cents. Each zone has a configurable tariff from 5 to 100 cents per started hour. Cancellation is owner-only, idempotency-safe through booking status checks, and releases the slot only when no other confirmed booking remains.
