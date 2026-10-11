# MEP Websites

Three server-rendered websites for **fictional** engineering and MEP (mechanical, electrical, plumbing) businesses, built with **Java 21, Spring Boot 3 and Thymeleaf**. Each one targets a different kind of company and has its own brand, layout and content strategy. They share a small common library so that forms, validation and layout plumbing aren't duplicated.

| Site | Business type | Port | Highlights |
| --- | --- | --- | --- |
| [Meridian MEP Contracting](meridian-contracting/README.md) | Turnkey MEP contractor | 8091 | Project portfolio with sector filters, service detail pages, RFQ form with discipline selection |
| [Axis Engineering Consultants](axis-consultants/README.md) | MEP design & BIM consultancy | 8092 | Editorial case studies, design-fee estimator, project brief form |
| [Thermiq HVAC Services](thermiq-hvac/README.md) | HVAC service company | 8093 | Online booking with slot capacity (JPA + H2), AC sizing calculator, plan pricing |

> Every company, person, project, review and credential on these sites is invented. Each page shows a demo notice saying so.

## Structure

```
mep-websites/
├── pom.xml                  Maven parent (Spring Boot 3.3, Java 21)
├── mep-common/              Shared library (auto-configured, not a runnable app)
├── meridian-contracting/    Site 1
├── axis-consultants/        Site 2
├── thermiq-hvac/            Site 3 (the only one with a database)
├── Dockerfile               Builds any site: --build-arg MODULE=<module>
└── docker-compose.yml       Runs all three sites on their own
```

Each site follows the same layering:

```
config/       Spring configuration, @ConfigurationProperties
model/        Records, enums and (Thermiq only) JPA entities
repository/   JSON-backed content repositories, or Spring Data JPA
service/      Business logic — catalogues, calculators, booking rules
web/          Controllers, form beans (Jakarta Bean Validation)
resources/
  content/    Site copy as JSON, editable without touching Java
  templates/  Thymeleaf: layouts/, fragments/, pages, error pages
  static/     Site-specific CSS, JS and images
```

### What `mep-common` provides

`mep-common` is a Spring Boot auto-configuration. Adding the dependency gives a site:

- **`CompanyProfile`**, bound from the `company.*` properties, plus `SiteModelAdvice`, which exposes `company`, `currentPath` and `year` to every template.
- **Fragments** in `templates/common/fragments.html`: Bootstrap from WebJars, an SVG favicon, the demo notice, field errors and the form error summary.
- **`@EgyptianPhone`**, a Bean Validation constraint for mobile and landline numbers.
- **`InquiryNotifier`**, which is how form submissions leave the app. The default implementation logs them. To send email or post to a CRM, provide your own bean.
- **`ReferenceGenerator`** for human-readable reference numbers, plus a `Clock` bean so tests can fix the time.
- **`mep-messages.properties`** with readable messages for type-conversion errors.

The shared code covers plumbing only. Every site has its own layout, CSS, fonts and JavaScript, so they look and behave differently.

## Prerequisites

Choose one:

- **Docker** (Docker Desktop on Windows/macOS). You don't need anything else.
- **JDK 21 + Maven 3.9** to run the sites directly.

## Run

### With Docker

```bash
cd mep-websites
docker compose up --build
```

Then open <http://localhost:8091>, <http://localhost:8092> and <http://localhost:8093>.

The root `docker-compose.yml` of this repository also runs the sites behind the portfolio gateway at `http://localhost:3099/mep/meridian/`, `/mep/axis/` and `/mep/thermiq/`.

### With Maven

```bash
cd mep-websites
mvn -pl meridian-contracting -am spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl axis-consultants -am spring-boot:run -Dspring-boot.run.profiles=dev
mvn -pl thermiq-hvac -am spring-boot:run -Dspring-boot.run.profiles=dev
```

The `dev` profile turns off template and static-resource caching, so edits show up on refresh.

### Without a local JDK

```bash
docker run --rm -v "$PWD":/workspace -v mep-m2:/root/.m2 -w /workspace \
  maven:3.9-eclipse-temurin-21-alpine mvn -B test
```

## Tests

```bash
mvn test
```

The suite has about 120 tests:

- **Unit tests** cover the business logic: phone validation, reference numbers, project and case-study filtering, the fee estimator, the cooling-load calculator, plan pricing and the booking rules. The booking rules run against an in-memory H2 database.
- **MockMvc tests** (`@SpringBootTest`) render every page and submit every form. They check:
  - server-side validation errors;
  - redirect-after-post with a reference number;
  - honeypot spam handling;
  - 404 pages.

## Configuration

Settings that change per environment are externalised:

| Property / env var | Used by | Purpose |
| --- | --- | --- |
| `PORT` | all | HTTP port (defaults: 8091, 8092, 8093) |
| `SERVER_SERVLET_CONTEXT_PATH` | all | Mount a site under a prefix, e.g. `/mep/axis`. Links and redirects follow it. |
| `PORTFOLIO_URL` | all | Where the "built by" links point |
| `company.*` | all | Name, phone, email, address and opening hours (in `application.yml`) |
| `axis.fees.*` | Axis | Design-fee rates, complexity factors, area bands and minimum fee |
| `thermiq.pricing.*` | Thermiq | Maintenance plans and the yearly discount |
| `thermiq.booking.*`, `THERMIQ_SLOT_CAPACITY` | Thermiq | Crews per slot, booking horizon, closed days, lead time, time zone |
| `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` | Thermiq | Database connection. Defaults to a local H2 file in `THERMIQ_DATA_DIR`. |

There are no secrets in the source. The only credentials are the database user and password, and they are read from the environment.

## Adding another site

1. Copy a module's `pom.xml`, change the `artifactId` and add the module to the parent `pom.xml`.
2. Add a `@SpringBootApplication` class and an `application.yml` with a `company:` block.
3. Write `templates/layouts/main.html` using the `common/fragments` fragments, plus your own CSS.
4. Add a `COPY <module>/pom.xml` line to the `Dockerfile`.
