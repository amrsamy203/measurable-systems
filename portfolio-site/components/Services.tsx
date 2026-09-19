import { services } from "@/lib/content";
import styles from "./Services.module.css";

export function Services() {
  return (
    <section id="services" className={`section ${styles.section}`}>
      <div className="container">
        <p className="section-label">Services</p>
        <h2 className="section-title">Packages for backends that move work.</h2>
        <p className="section-lead">
          Mid-tier specialist posture — scoped milestones for APIs, queues,
          integrations, and batch performance. Pricing discussed per engagement.
        </p>

        <div className={styles.grid}>
          {services.map((svc) => (
            <article key={svc.name} className={styles.pkg}>
              <h3 className={styles.name}>{svc.name}</h3>
              <p className={styles.for}>
                <span className={styles.forLabel}>For</span> {svc.for}
              </p>
              <p className={styles.includes}>{svc.includes}</p>
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}
