import styles from "./HeroVisual.module.css";

/** Full-bleed industrial topology: queues, routes, metric nodes — CSS/SVG only. */
export function HeroVisual() {
  return (
    <div className={styles.wrap}>
      <svg
        className={styles.svg}
        viewBox="0 0 1440 900"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        preserveAspectRatio="xMidYMid slice"
      >
        <defs>
          <linearGradient id="gridFade" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stopColor="#2DD4BF" stopOpacity="0.18" />
            <stop offset="55%" stopColor="#2DD4BF" stopOpacity="0.05" />
            <stop offset="100%" stopColor="#F59E0B" stopOpacity="0.08" />
          </linearGradient>
          <linearGradient id="pipe" x1="0" y1="0" x2="1" y2="0">
            <stop offset="0%" stopColor="#2DD4BF" stopOpacity="0" />
            <stop offset="40%" stopColor="#2DD4BF" stopOpacity="0.7" />
            <stop offset="100%" stopColor="#F59E0B" stopOpacity="0.55" />
          </linearGradient>
        </defs>

        {/* Topology grid */}
        <g className={styles.grid} stroke="url(#gridFade)" strokeWidth="1">
          {Array.from({ length: 14 }).map((_, i) => (
            <line
              key={`v${i}`}
              x1={180 + i * 90}
              y1="40"
              x2={180 + i * 90}
              y2="860"
            />
          ))}
          {Array.from({ length: 10 }).map((_, i) => (
            <line
              key={`h${i}`}
              x1="120"
              y1={80 + i * 80}
              x2="1380"
              y2={80 + i * 80}
            />
          ))}
        </g>

        {/* Pipeline paths — draw-in */}
        <g className={styles.paths} stroke="url(#pipe)" strokeWidth="1.5" fill="none">
          <path
            className={styles.pathA}
            d="M220 220 H480 L560 300 H820 L900 220 H1180"
          />
          <path
            className={styles.pathB}
            d="M260 420 H520 L640 540 H980 L1080 460 H1280"
          />
          <path
            className={styles.pathC}
            d="M300 680 H640 L760 600 H1100"
          />
        </g>

        {/* Queue nodes */}
        <g className={styles.nodes}>
          <rect x="460" y="200" width="40" height="40" className={styles.nodeTeal} />
          <rect x="800" y="280" width="40" height="40" className={styles.nodeAmber} />
          <rect x="500" y="400" width="40" height="40" className={styles.nodeTeal} />
          <rect x="960" y="520" width="40" height="40" className={styles.nodeAmber} />
          <rect x="620" y="660" width="40" height="40" className={styles.nodeTeal} />
          <rect x="1080" y="580" width="40" height="40" className={styles.nodeTeal} />
        </g>

        {/* Metric readouts */}
        <g className={styles.metrics} fontFamily="IBM Plex Mono, monospace">
          <text x="880" y="190" fill="#2DD4BF" fontSize="12" opacity="0.85">
            p95 42ms
          </text>
          <text x="1120" y="440" fill="#F59E0B" fontSize="12" opacity="0.8">
            q.depth 128
          </text>
          <text x="720" y="640" fill="#94A3B8" fontSize="12" opacity="0.7">
            ingest → route → assign
          </text>
        </g>
      </svg>

      <div className={styles.vignette} />
    </div>
  );
}
