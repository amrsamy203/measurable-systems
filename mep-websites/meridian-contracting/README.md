# Meridian MEP Contracting (demo)

The website of a **fictional** turnkey MEP contractor in Cairo. It is aimed at developers and main contractors who are shortlisting subcontractors, so it puts capacity, certifications and completed projects first.

- **Stack:** Java 21, Spring Boot 3.3, Spring MVC, Thymeleaf with the Layout Dialect, Bootstrap 5 (WebJars), Jakarta Bean Validation.
- **Persistence:** none. Projects and services are JSON content files loaded by `@Repository` classes, because the site has no data that changes at runtime.
- **Port:** 8091

## Pages

| URL | What it shows |
| --- | --- |
| `/` | Hero with key figures, services, delivery process, featured projects, sectors, quote |
| `/about` | Company story, values, timeline, leadership, certifications (`#certifications`), HSE |
| `/services`, `/services/{slug}` | Six disciplines (HVAC, electrical, plumbing, fire, low current, BMS) with scope, systems, standards and related projects |
| `/projects?sector=` | Project portfolio with a server-side sector filter |
| `/projects/{slug}` | Case study: challenge, approach, results, key facts, related projects |
| `/contact` | Request-for-quotation form; `?service=` and `?sector=` pre-fill it |
| `/contact/thank-you` | Confirmation with an RFQ reference such as `MER-RFQ-2026-0001` |

## The quotation form

`QuoteRequestForm` is validated on the server:

- Required fields.
- An Egyptian phone format, using `@EgyptianPhone` from `mep-common`.
- Area limits and message length.
- Discipline codes are checked against the service catalogue.
- A hidden honeypot field filters out simple bots.

Valid requests go to `QuoteService`, which issues a reference and passes the details to the `InquiryNotifier`. The controller then redirects (Post/Redirect/Get), so refreshing the page doesn't resubmit.

## Run

```bash
# from mep-websites/
mvn -pl meridian-contracting -am spring-boot:run -Dspring-boot.run.profiles=dev
# or
docker compose up --build meridian
```

Open <http://localhost:8091>.

## Configure

All settings are in `src/main/resources/application.yml`:

- `company.*`: name, contact details and opening hours.
- `PORT`: the HTTP port.
- `SERVER_SERVLET_CONTEXT_PATH`: the path prefix when the site runs behind a gateway.
- `PORTFOLIO_URL`: where the "built by" links point.

Site copy is in `src/main/resources/content/projects.json` and `services.json`.

## Test

```bash
mvn -pl meridian-contracting -am test
```

- **`ProjectCatalogServiceTest`** covers filtering, the related-projects order, sector counts and the 404 case.
- **`WebsiteTest`** (MockMvc) renders every page and covers the quotation form: validation errors, the redirect with a reference, the honeypot and the thank-you guard.
