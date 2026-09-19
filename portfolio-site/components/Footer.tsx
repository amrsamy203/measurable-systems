import { site } from "@/lib/content";
import styles from "./Footer.module.css";

export function Footer() {
  const year = new Date().getFullYear();

  return (
    <footer className={styles.footer}>
      <div className={`container ${styles.inner}`}>
        <div>
          <p className={styles.brand}>{site.name}</p>
          <p className={styles.theme}>{site.theme}</p>
        </div>
        <p className={styles.copy}>
          © {year} · Cairo, Egypt · Backend systems under real volume
        </p>
      </div>
    </footer>
  );
}
