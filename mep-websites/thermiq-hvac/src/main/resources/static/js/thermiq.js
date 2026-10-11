(() => {
    const header = document.getElementById("siteHeader");
    if (header) {
        const onScroll = () => header.classList.toggle("scrolled", window.scrollY > 8);
        onScroll();
        window.addEventListener("scroll", onScroll, { passive: true });
    }

    // After a calculation the result sits below the form on phones; bring it into view.
    const result = document.getElementById("result");
    if (result && window.matchMedia("(max-width: 991.98px)").matches) {
        result.scrollIntoView({ behavior: "smooth", block: "start" });
    }
})();
