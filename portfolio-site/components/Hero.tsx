import { site } from "@/lib/content";
import { HeroVisual } from "./HeroVisual";
import styles from "./Hero.module.css";

export function Hero() {
  return (
    <section className={styles.hero} aria-labelledby="hero-brand">
      <div className={styles.visual} aria-hidden>
        <HeroVisual />
      </div>

      <div className={`container ${styles.content}`}>
        <p className={styles.eyebrow}>
          <span className={styles.dot} />
          {site.theme} · {site.location}
        </p>

        <h1 id="hero-brand" className={styles.brand}>
          {site.name}
        </h1>

        <p className={styles.headline}>
          Backend systems that hold up under real volume.
        </p>

        <p className={styles.support}>
          Queues, rules, integrations, and APIs that turn slow, fragile business
          processes into fast, reliable pipelines.
        </p>

        <div className={styles.ctas}>
          <a href="#contact" className="btn btn-primary">
            Contact
          </a>
          <a href="#work" className="btn btn-ghost">
            View work
          </a>
        </div>
      </div>
    </section>
  );
}
