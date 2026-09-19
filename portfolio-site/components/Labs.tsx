import Link from "next/link";
import styles from "./Labs.module.css";

export function Labs() {
  return (
    <section id="labs" className={`section ${styles.section}`}>
      <div className="container">
        <p className="section-label">Labs</p>
        <h2 className="section-title">Measurable Systems</h2>
        <p className="section-lead">
          Portfolio demos are built so buyers can see throughput, failure
          injection, and architecture decisions — not just screenshots of CRUD.
        </p>

        <div className={styles.panel}>
          <div className={styles.diagram} aria-hidden>
            <div className={styles.queue}>
              <span className={styles.queueLabel}>ingest</span>
              <div className={styles.slots}>
                <i className={styles.pulse} />
                <i />
                <i />
                <i className={styles.pulseDelay} />
              </div>
            </div>
            <div className={styles.arrow}>→</div>
            <div className={styles.queue}>
              <span className={styles.queueLabel}>route</span>
              <div className={styles.slots}>
                <i className={styles.pulseDelay} />
                <i className={styles.pulse} />
                <i />
              </div>
            </div>
            <div className={styles.arrow}>→</div>
            <div className={styles.queue}>
              <span className={styles.queueLabel}>assign</span>
              <div className={styles.slots}>
                <i />
                <i className={styles.pulse} />
                <i className={styles.pulseDelay} />
                <i />
              </div>
            </div>
          </div>

          <div className={styles.copy}>
            <h3 className={styles.sub}>Throughput Console</h3>
            <p>
              Live metrics for events/sec, p95 latency, queue depth, and failure
              rate — the same language used in CaseFlow and DispatchGrid demos.
            </p>
            <ul className={styles.links}>
              <li>
                <Link href="/work/caseflow">CaseFlow console concepts →</Link>
              </li>
              <li>
                <Link href="/work/dispatchgrid">
                  DispatchGrid throughput view →
                </Link>
              </li>
              <li>
                <span className={styles.soon}>Live lab sandbox — coming soon</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>
  );
}
