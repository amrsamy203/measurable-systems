import Link from "next/link";
import type { projects } from "@/lib/content";
import styles from "./CaseStudy.module.css";

type Project = (typeof projects)[number];

export function CaseStudy({ project }: { project: Project }) {
  return (
    <article className={styles.page}>
      <div className="container">
        <Link href="/#work" className={styles.back}>
          ← Selected work
        </Link>

        <header className={styles.header}>
          <p className={styles.eyebrow}>Case study</p>
          <h1 className={styles.title}>{project.name}</h1>
          <p className={styles.tagline}>{project.tagline}</p>
          <p className={styles.outcome}>{project.outcome}</p>
        </header>

        <div className={styles.grid}>
          <section>
            <h2>Problem</h2>
            <p>{project.problem}</p>
          </section>
          <section>
            <h2>Solution</h2>
            <p>{project.solution}</p>
          </section>
        </div>

        <section className={styles.block}>
          <h2>Highlights</h2>
          <ul>
            {project.highlights.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </section>

        <section className={styles.block}>
          <h2>Tech</h2>
          <ul className={styles.tech}>
            {project.tech.map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </section>

        <section className={styles.demo}>
          <h2>Demo</h2>
          <p className={styles.demoNote}>{project.demoNote}</p>
          <p className={styles.demoHint}>
            Placeholder for interactive Throughput Console / API playground.
            Architecture and metrics concepts are described above.
          </p>
        </section>
      </div>
    </article>
  );
}
