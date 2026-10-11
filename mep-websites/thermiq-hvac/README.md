# Thermiq HVAC Services (demo)

The website of a **fictional** AC installation, maintenance and repair company covering Greater Cairo. It targets homeowners and small businesses, so the goal is conversion: clear prices, a next-free-slot badge, online booking, and a sticky Call / WhatsApp / Book bar on mobile.

- **Stack:** Java 21, Spring Boot 3.3, Spring MVC, Thymeleaf with the Layout Dialect, Bootstrap 5 (WebJars), Jakarta Bean Validation, **Spring Data JPA with H2**.
- **Port:** 8093

Bookings are the only data that has to persist, so this is the only one of the three sites with a database.

## Pages

| URL | What it shows |
| --- | --- |
| `/` | Hero with the live next free slot, services with starting prices, how it works, plans, recent jobs, reviews, coverage areas, FAQ |
| `/services` | Services and prices, capabilities, industries served |
| `/plans?billing=yearly&units=3` | Maintenance plans, priced on the server for the chosen billing period and number of units |
| `/calculator` | AC sizing: room area, room type, sun exposure and occupants give a recommended HP and BTU/h |
| `/book` | Six-day slot availability plus the booking form; `?service=&date=&slot=&units=` pre-fill it |
| `/book/confirmed` | Confirmation with a reference such as `TQ-2026-000042` |
| `/about` | Story, figures, certifications (`#certifications`), coverage (`#coverage`) |
| `/contact` | General and business enquiries |

## Booking rules

The form beans check the shape of the input: required fields, an Egyptian **mobile** number, and 1–10 units. Coverage is enforced by type, because the area is an enum of the districts served.

`BookingService` applies the business rules, using the injected `Clock` and the `thermiq.booking.*` policy:

- Dates in the past are rejected.
- Dates beyond the booking horizon (30 days) are rejected.
- Closed days (Fridays) are rejected.
- A same-day slot must start at least `min-lead-hours` from now. "Now" is evaluated in `Africa/Cairo`, whatever the server's time zone.
- Each slot takes at most `slot-capacity` bookings (the number of crews).

When a rule refuses a booking, the message appears on the matching form field. The booking reference comes from the database id. The slot grid on `/book` is built from one grouped count query.

## Run

```bash
# from mep-websites/
mvn -pl thermiq-hvac -am spring-boot:run -Dspring-boot.run.profiles=dev
# or
docker compose up --build thermiq
```

Open <http://localhost:8093>. Bookings are stored in `./data/thermiq.mv.db`, or in the `thermiq_data` volume when running in Docker.

## Configure

| Setting | Default | Notes |
| --- | --- | --- |
| `DATABASE_URL` | `jdbc:h2:file:${THERMIQ_DATA_DIR}/thermiq` | For PostgreSQL, add the driver dependency and set the URL |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | `sa` / empty | Supply real credentials through the environment |
| `THERMIQ_DATA_DIR` | `./data` | Where the H2 file lives |
| `THERMIQ_SLOT_CAPACITY` | `3` | Crews per time slot |
| `thermiq.booking.*` | see `application.yml` | Horizon, closed days, lead time, time zone |
| `thermiq.pricing.*` | see `application.yml` | Plans, prices, yearly discount |

The H2 web console is disabled.

## Test

```bash
mvn -pl thermiq-hvac -am test
```

- **`CoolingLoadCalculatorTest`** covers sizing, unit-size boundaries and multi-unit spaces.
- **`PlanPricingTest`** covers per-unit pricing, the yearly discount and saving, and unit clamping.
- **`BookingServiceTest`** (`@DataJpaTest` with a fixed clock) covers:
  - every booking rule;
  - slot capacity;
  - availability and the next free slot.
- **`WebsiteTest`** (MockMvc) renders every page and covers:
  - the calculator and its validation;
  - a booking that is stored and confirmed;
  - field errors from the business rules;
  - the honeypot and the contact form.
