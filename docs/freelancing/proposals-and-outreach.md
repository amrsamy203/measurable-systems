# Proposal Templates

## Upwork / Freelancer — short proposal

```
Hi {{client_name}},

You need {{echo_their_problem_in_one_line}}.

I specialize in Java/Spring backends for queue-driven workflows, fair routing, and high-volume integrations. Relevant proof (anonymized): cut a production processing pipeline from ~45 minutes to ~2 minutes; built campaign-style systems involved in 20M+ messages per run.

Closest public demo: {{CaseFlow | DispatchGrid | RelateAI}} — {{one sentence what it shows}}.
Live: {{demo_url}}

Suggested first milestone (fixed):
1. {{deliverable_1}}
2. {{deliverable_2}}
3. Short decision notes + Docker run instructions

Timeline: {{N}} days | Price: ${{amount}}

Three questions so I can refine the estimate:
1. {{discovery_q1}}
2. {{discovery_q2}}
3. {{discovery_q3}}

Happy to adjust scope to a thin vertical slice first.
— Amr
```

## Discovery questions bank

**Workflow / cases**
1. Who creates work items, and who must receive them?
2. What are the assignment rules today (round-robin, skills, priority, manual)?
3. What is an acceptable SLA from creation to first assignment?

**Integrations / campaigns**
1. Which providers/APIs must we call, and what are their rate limits?
2. How should retries and permanent failures be handled (DLQ, alert, replay)?
3. Do you need delivery receipts / webhooks back into your system?

**Performance**
1. What is current volume (events/day) and peak?
2. What is the slow path today (DB, external API, single-threaded job)?
3. What metric defines success (p95 latency, job duration, error rate)?

**AI feature**
1. What input goes to the model, and what must be persisted?
2. Rate limits / cost caps per tenant?
3. Fallback when the model provider is down?

## Client outreach (LinkedIn / email)

```
Subject: Backend help for {{queues | integrations | slow batch jobs}}

Hi {{name}},

I noticed {{company}} {{signal}}. I help teams ship Spring Boot APIs and worker pipelines that stay reliable under load — with clear metrics, not only “it works on my machine.”

If useful, I can share a 2-minute demo of CaseFlow (rule-based case routing + live throughput console) or DispatchGrid (multi-provider dispatch with retries/rate limits).

No pitch deck — just a short call if you have an active backend bottleneck.

Amr Samy
Cairo | Java/Spring backend
{{portfolio_url}}
```

## Job targeting filters

**Apply when the post mentions:** Spring Boot, Java API, RabbitMQ/Kafka/SQS, microservices, integrations, SMS/email APIs, webhooks, performance, batch jobs, background workers.

**Skip:** logo-only, WordPress theme tweaks, pure mobile UI, “build Uber clone from scratch” with unrealistic budgets.

## Weekly rhythm (employed + freelancing)

- 7–10 hrs: portfolio/project polish  
- 3–5 hrs: proposals / Khamsat responses / outreach  
- Aim: 5–10 quality proposals/week, not 50 generic bids  
