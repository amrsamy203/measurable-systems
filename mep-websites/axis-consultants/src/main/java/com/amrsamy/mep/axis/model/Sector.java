package com.amrsamy.mep.axis.model;

import java.util.Arrays;
import java.util.Optional;

public enum Sector {
    WORKPLACE("Workplace & civic"),
    HEALTHCARE("Healthcare"),
    HOSPITALITY("Hospitality"),
    EDUCATION("Education & research"),
    MISSION_CRITICAL("Mission critical");

    private final String label;

    Sector(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getSlug() {
        return name().toLowerCase().replace('_', '-');
    }

    public static Optional<Sector> fromSlug(String slug) {
        return Arrays.stream(values()).filter(s -> s.getSlug().equals(slug)).findFirst();
    }
}
