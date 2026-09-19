import Link from "next/link";
import { projects } from "@/lib/content";
import styles from "./SelectedWork.module.css";

export function SelectedWork() {
  return (
    <section id="work" className={`section ${styles.section}`}>
      <div className="container">
        <p className="section-label">Selected work</p>
        <h2 className="section-title">Outcomes first. Architecture second.</h2>
        <p className="section-lead">
          Anonymized systems patterned on production backends — routing, dispatch,
          and AI feature endpoints you can evaluate by results.
        </p>

        <ul className={styles.list}>
          {projects.map((project, index) => (
            <li key={project.slug} className={styles.item}>
              <div className={styles.index}>
                <span className="mono">0{index + 1}</span>
              </div>
              <div className={styles.body}>
                <h3 className={styles.name}>{project.name}</h3>
                <p className={styles.tagline}>{project.tagline}</p>
                <p className={styles.outcome}>{project.outcome}</p>
                <Link href={`/work/${project.slug}`} className={styles.link}>
                  Case study
                  <span aria-hidden> →</span>
                </Link>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </section>
  );
}
