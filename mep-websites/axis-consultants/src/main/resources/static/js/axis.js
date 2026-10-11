(() => {
    const header = document.getElementById("siteHeader");
    if (header) {
        const onScroll = () => header.classList.toggle("scrolled", window.scrollY > 8);
        onScroll();
        window.addEventListener("scroll", onScroll, { passive: true });
    }

    // The estimator's slider is a progressive enhancement: it has no name, so only the number
    // input is submitted and validated on the server.
    document.querySelectorAll("input[type=range][data-sync]").forEach((range) => {
        const input = document.getElementById(range.dataset.sync);
        if (!input) return;
        range.hidden = false;
        const follow = () => {
            range.value = input.value ? Math.min(Number(input.value), Number(range.max)) : range.min;
        };
        follow();
        range.addEventListener("input", () => { input.value = range.value; });
        input.addEventListener("input", follow);
    });

    // Bring a freshly calculated estimate into view on small screens.
    const estimate = document.getElementById("estimate");
    if (estimate && window.matchMedia("(max-width: 991.98px)").matches) {
        estimate.scrollIntoView({ behavior: "smooth", block: "start" });
    }
})();
