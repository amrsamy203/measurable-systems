# Axis Engineering Consultants (demo)

The website of a **fictional** independent MEP design and BIM consultancy. Its audience is architects and developers choosing a design consultant, so it reads like an engineering monograph: editorial typography, case studies with measurable outcomes, and a transparent fee estimator.

- **Stack:** Java 21, Spring Boot 3.3, Spring MVC, Thymeleaf with the Layout Dialect, Bootstrap 5 (WebJars), Jakarta Bean Validation.
- **Persistence:** none. Case studies, disciplines and team are JSON content; fee rates live in configuration.
- **Port:** 8092

## Pages

| URL | What it shows |
| --- | --- |
| `/` | Hero, studio figures, selected work, discipline index, design stages, estimator teaser, memberships |
| `/work?sector=&stage=` | Case studies filtered by sector and design stage (the two filters combine) |
| `/work/{slug}` | Case study: brief, approach, outcomes, stages covered, disciplines, tools, next project |
| `/expertise` | Six disciplines with scope, tools and standards; the five design stages |
| `/studio` | Story, principles, team, accreditations (`#accreditations`) |
| `/fee-estimator` | Design-fee estimator (form plus server-side calculation) |
| `/contact` | Project brief form; the estimator pre-fills it with `?area=&disciplines=` |

## Fee estimator

`FeeEstimator` is a plain service with no web dependencies, and `FeeEstimatorTest` tests it directly. It works in five steps:

1. **Area bands.** The first 10,000 m² is priced at 100%, the next 40,000 m² at 85% and anything above 50,000 m² at 70%.
2. **Discipline fees.** For each discipline: effective area × rate per m² × complexity factor (standard, enhanced or critical).
3. **Add-ons.** BIM coordination and site supervision are added as a share of the design fee.
4. **Rounding and minimum.** The total is rounded to the nearest EGP 1,000, with a minimum commission.
5. **Range.** The visitor sees an indicative range of −10% to +15%.

The rates, factors, bands and minimum all come from `axis.fees.*` in `application.yml`, so they can be changed per environment without a code change.

## Run

```bash
# from mep-websites/
mvn -pl axis-consultants -am spring-boot:run -Dspring-boot.run.profiles=dev
# or
docker compose up --build axis
```

Open <http://localhost:8092>.

## Configure

- `company.*`: studio details.
- `axis.fees.*`: estimator inputs. You can also set them through environment variables, e.g. `AXIS_FEES_MINIMUMFEE=150000`.
- `PORT`, `SERVER_SERVLET_CONTEXT_PATH`, `PORTFOLIO_URL`.
- Content files: `src/main/resources/content/work.json`, `disciplines.json` and `team.json`.

## Test

```bash
mvn -pl axis-consultants -am test
```

- **`FeeEstimatorTest`** covers area bands, discipline lines, complexity, add-ons, the minimum fee, rounding and a missing-rate configuration.
- **`CaseStudyServiceTest`** covers the combined filters, the wrap-around "next" link and the 404 case.
- **`WebsiteTest`** (MockMvc) renders every page and covers:
  - the estimator results and its validation, including type-mismatch messages;
  - pre-filling the brief form from the estimator;
  - brief validation, the redirect with a reference, and the honeypot.
