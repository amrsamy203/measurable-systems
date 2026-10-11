package com.amrsamy.mep.thermiq.model;

import java.util.Arrays;
import java.util.Optional;

public enum ServiceType {
    INSTALLATION("AC installation", "New split, cassette or ducted units, including copper piping and drainage.", 1200, "fan"),
    MAINTENANCE("Cleaning & maintenance", "Deep clean of filters, coils and drain lines, with a performance check.", 350, "spray"),
    REPAIR("Repair & fault finding", "Not cooling, leaking water or showing an error code — diagnosed on the first visit.", 300, "wrench"),
    GAS("Refrigerant top-up & leak test", "Pressure test, leak repair and recharge with the correct refrigerant.", 650, "gauge"),
    INSPECTION("Pre-summer inspection", "A 20-point check so your units are ready before the heat arrives.", 250, "check");

    private final String label;
    private final String description;
    private final int fromPrice;
    private final String icon;

    ServiceType(String label, String description, int fromPrice, String icon) {
        this.label = label;
        this.description = description;
        this.fromPrice = fromPrice;
        this.icon = icon;
    }

    public String getLabel() { return label; }
    public String getDescription() { return description; }
    public int getFromPrice() { return fromPrice; }
    public String getIcon() { return icon; }

    public String getSlug() {
        return name().toLowerCase();
    }

    public static Optional<ServiceType> fromSlug(String slug) {
        return Arrays.stream(values()).filter(s -> s.getSlug().equalsIgnoreCase(String.valueOf(slug))).findFirst();
    }
}
