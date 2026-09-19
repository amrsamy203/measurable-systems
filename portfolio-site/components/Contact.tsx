import { site } from "@/lib/content";
import styles from "./Contact.module.css";

export function Contact() {
  return (
    <section id="contact" className={`section ${styles.section}`}>
      <div className="container">
        <p className="section-label">Contact</p>
        <h2 className="section-title">Tell me about the pipeline that breaks.</h2>
        <p className="section-lead">
          Based in {site.location}. Prefer clear scope: queues, integrations,
          batch latency, or an API MVP.
        </p>

        <div className={styles.channels}>
          <a href={`mailto:${site.email}`} className={styles.channel}>
            <span className={styles.channelLabel}>Email</span>
            <span className={styles.channelValue}>{site.email}</span>
          </a>
          <a href={site.phoneHref} className={styles.channel}>
            <span className={styles.channelLabel}>Phone</span>
            <span className={styles.channelValue}>{site.phone}</span>
          </a>
          <div className={styles.channel}>
            <span className={styles.channelLabel}>Location</span>
            <span className={styles.channelValue}>{site.location}</span>
          </div>
        </div>

        <a href={`mailto:${site.email}`} className={`btn btn-primary ${styles.cta}`}>
          Email Amr
        </a>
      </div>
    </section>
  );
}
