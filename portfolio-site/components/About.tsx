import { MetricCounter } from "./MetricCounter";
import styles from "./About.module.css";

export function About() {
  return (
    <section id="about" className={`section ${styles.section}`}>
      <div className="container">
        <p className="section-label">About</p>
        <div className={styles.layout}>
          <div>
            <h2 className="section-title">
              Five-plus years building backends where latency and volume matter.
            </h2>
            <p className={styles.copy}>
              I design for concurrency, failure modes, and fair routing — not only
              CRUD endpoints. Clients hire me when campaigns, cases, alarms, or
              legacy bridges need pipelines that stay measurable under load.
            </p>
            <p className={styles.copy}>
              Competitive programming background (ICPC / ECPC) shaped how I think
              about algorithms and edge cases; production work taught me queues,
              retries, and observability.
            </p>
            <ul className={styles.stack}>
              {[
                "Java",
                "Spring Boot",
                "PostgreSQL",
                "RabbitMQ",
                "Redis",
                "Docker",
                "Multi-cloud",
              ].map((item) => (
                <li key={item}>{item}</li>
              ))}
            </ul>
          </div>

          <div className={styles.metrics}>
            <MetricCounter
              value={45}
              prefix="~"
              suffix=" min → ~2 min"
              label="Detection pipeline latency cut (anonymized)"
            />
            <MetricCounter
              value={20}
              suffix="M+"
              label="Messages through campaign systems"
            />
            <MetricCounter
              value={5}
              suffix="+"
              label="Years backend engineering"
            />
          </div>
        </div>
      </div>
    </section>
  );
}
