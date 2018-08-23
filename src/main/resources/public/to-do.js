window.addEventListener("load", () => {
    document.querySelectorAll("input[type=\"checkbox\"]").forEach(checkbox => {
        checkbox.addEventListener("change", () => {
            const item = checkbox.parentElement;
            item.style.marginLeft = "100px";
            item.style.opacity = 0;
            window.setTimeout(() => item.remove(), 300);
        });
    });
});
