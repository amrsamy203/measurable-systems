export const site = {
  name: "Amr Samy",
  title: "Amr Samy — Measurable Systems",
  description:
    "Backend systems that turn slow, fragile business processes into fast, reliable pipelines — queues, rules, integrations, and APIs that hold up under real volume.",
  location: "Cairo, Egypt",
  email: "samyamr270@gmail.com",
  phone: "+(20)1016530911",
  phoneHref: "tel:+201016530911",
  theme: "Measurable Systems",
  /** Temporary Vercel URL — claim ASAP or redeploy after login */
  liveUrl: "https://temporary-swift-nebula-ysdu6bj.vercel.app",
  claimUrl:
    "https://vercel.com/claim-deployment?code=2b32cefa-a80f-4df1-bd18-28bc03e33193",
};

export const projects = [
  {
    slug: "caseflow",
    name: "CaseFlow",
    tagline: "Investigation cases routed fairly — under measurable load.",
    outcome:
      "Events become assigned work through queues and rules, with SLA visibility and a live Throughput Console.",
    problem:
      "Ops teams drown when cases pile up without fair assignment, clear routing, or visibility into queue depth and SLA risk.",
    solution:
      "A multi-tenant case engine that ingests events, evaluates routing rules, assigns agents round-robin with skill tags, tracks SLAs, and audits every transition — demoable via load simulation.",
    tech: [
      "Java / Spring Boot",
      "PostgreSQL",
      "RabbitMQ",
      "Redis",
      "JWT + RBAC",
      "Docker Compose",
      "Next.js ops board",
    ],
    highlights: [
      "Ingest → route → assign pipeline over RabbitMQ",
      "Round-robin assignment with skill-aware queues",
      "Metrics: events/sec, p95 latency, queue depth, failures",
      "Failure injection for honest demo scenarios",
    ],
    demoNote: "Local: http://localhost:8081 — admin@caseflow.demo / password",
    accent: "teal" as const,
  },
  {
    slug: "dispatchgrid",
    name: "DispatchGrid",
    tagline: "Campaign and notification dispatch that survives volume.",
    outcome:
      "Patterned after systems that pushed 20M+ messages — rate limits, retries, and provider adapters that fail gracefully.",
    problem:
      "Outbound campaigns stall or double-send when provider APIs flake, rate limits are ignored, or workers have no DLQ strategy.",
    solution:
      "A dispatch grid of workers that enqueue jobs, respect provider quotas, retry with backoff into DLQs, and surface throughput metrics so ops can see what is stuck.",
    tech: [
      "Java / Spring Boot",
      "RabbitMQ workers",
      "PostgreSQL",
      "Redis rate limiting",
      "Provider adapters",
      "Docker",
    ],
    highlights: [
      "Idempotent job processing",
      "Per-provider rate limits and circuit breaks",
      "Retry / DLQ paths for brittle integrations",
      "Ops-facing throughput and failure counters",
    ],
    demoNote: "Local: http://localhost:8082 — admin@dispatchgrid.demo / password",
    accent: "amber" as const,
  },
  {
    slug: "relateai",
    name: "RelateAI",
    tagline: "AI features as backend endpoints — not a rewrite.",
    outcome:
      "Daily AI challenges, social/topic graph insights, and media uploads as first-class API concerns.",
    problem:
      "Teams want an “AI feature” bolted onto an existing product, but prompt calls without graph context, storage, or audit trails become production liabilities.",
    solution:
      "A community backend: authenticated users, follows/topics/interactions, graph insight queries, daily challenges (OpenAI or mock), and local blob uploads behind a storage port.",
    tech: [
      "Java / Spring Boot",
      "PostgreSQL / H2",
      "OpenAI-compatible APIs",
      "Local blob adapter",
      "Docker",
    ],
    highlights: [
      "Graph insights: top connected, hottest topics, recommendations",
      "AI challenges with mock fallback when no API key",
      "Blob upload via storage port (local adapter)",
      "JWT auth and seeded demo community",
    ],
    demoNote: "Local: http://localhost:8083 — user1@relateai.demo / password",
    accent: "teal" as const,
  },
];

export const services = [
  {
    name: "API MVP",
    for: "New product or internal tool needing a solid first backend.",
    includes:
      "Domain model, REST + OpenAPI, JWT auth, Postgres schema, Docker Compose, basic tests.",
  },
  {
    name: "Queue Worker + Ops Surface",
    for: "Cases, jobs, or notifications that must run asynchronously.",
    includes:
      "RabbitMQ workers, retry/DLQ, assignment or dispatch rules, thin ops UI, metrics endpoint.",
  },
  {
    name: "Provider Integration",
    for: "Connect SMS, email, webhooks, payments, or legacy SOAP to your app.",
    includes:
      "Adapter strategy, rate limits, idempotency, failure notes, auth as required.",
  },
  {
    name: "Batch Optimization Audit",
    for: "Jobs that are too slow or unstable under real volume.",
    includes:
      "Measurement plan, bottleneck report, prioritized fixes — implementation quoted separately.",
  },
];

export function getProject(slug: string) {
  return projects.find((p) => p.slug === slug);
}
