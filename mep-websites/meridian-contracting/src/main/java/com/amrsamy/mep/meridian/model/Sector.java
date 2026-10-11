package com.amrsamy.mep.meridian.model;

import java.util.Arrays;
import java.util.Optional;

public enum Sector {
    HEALTHCARE("Healthcare", "Hospitals, clinics and labs with critical air and power."),
    EDUCATION("Education", "Schools and campuses built to tight academic calendars."),
    COMMERCIAL("Commercial & Retail", "Offices, malls and mixed-use towers."),
    HOSPITALITY("Hospitality", "Resorts and hotels where guest comfort never pauses."),
    INDUSTRIAL("Industrial", "Factories, cold stores and process utilities."),
    RESIDENTIAL("Residential", "Compounds and towers delivered building by building."),
    DATA_CENTRES("Data Centres", "Mission-critical cooling and 2N power.");

    private final String label;
    private final String description;

    Sector(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return name().toLowerCase().replace('_', '-');
    }

    public static Optional<Sector> fromSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(s -> s.getSlug().equalsIgnoreCase(slug.trim())).findFirst();
    }
}
