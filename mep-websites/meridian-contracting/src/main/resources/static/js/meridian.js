(() => {
  const header = document.getElementById("siteHeader");
  const onScroll = () => header && header.classList.toggle("scrolled", window.scrollY > 8);
  onScroll();
  window.addEventListener("scroll", onScroll, { passive: true });

  const reduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

  // Count-up for hero statistics, once they scroll into view.
  const counters = document.querySelectorAll("[data-count]");
  const animate = (el) => {
    const target = parseFloat(el.dataset.count);
    const decimals = parseInt(el.dataset.decimals || "0", 10);
    if (reduced || Number.isNaN(target)) return;
    const start = performance.now();
    const duration = 1400;
    const step = (now) => {
      const t = Math.min(1, (now - start) / duration);
      const eased = 1 - Math.pow(1 - t, 3);
      el.textContent = (target * eased).toFixed(decimals);
      if (t < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  };

  // Reveal sections as they enter the viewport.
  const revealables = document.querySelectorAll(".service-card, .feature-card, .project-card, .process li, .sector-list li, .value-card, .cert-grid li");
  if ("IntersectionObserver" in window) {
    const io = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        const el = entry.target;
        if (el.dataset.count !== undefined) animate(el);
        el.classList.add("in");
        io.unobserve(el);
      });
    }, { threshold: 0.15 });
    counters.forEach((el) => io.observe(el));
    if (!reduced) {
      revealables.forEach((el, i) => {
        el.classList.add("reveal");
        el.style.transitionDelay = `${(i % 3) * 80}ms`;
        io.observe(el);
      });
    }
  }
})();
