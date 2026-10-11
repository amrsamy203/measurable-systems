package com.amrsamy.mep.axis.model;

import java.util.Arrays;
import java.util.Optional;

/** RIBA-style work stages, simplified to how clients in the region usually procure MEP design. */
public enum Stage {
    CONCEPT("Concept design", "Load estimates, plant space, risers and a services strategy the architect can plan around."),
    SCHEMATIC("Schematic design", "System selection, single-line diagrams and coordinated layouts at 1:200."),
    DETAILED("Detailed design", "Calculated, sized and coordinated drawings with a clash-free BIM model."),
    TENDER("Tender documents", "Specifications, bills of quantities and technical bid evaluation."),
    SUPERVISION("Site supervision", "Shop-drawing review, inspections, witnessing tests and handover.");

    private final String label;
    private final String description;

    Stage(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getNumber() {
        return "%02d".formatted(ordinal() + 1);
    }

    public String getSlug() {
        return name().toLowerCase();
    }

    public static Optional<Stage> fromSlug(String slug) {
        return Arrays.stream(values()).filter(s -> s.getSlug().equals(slug)).findFirst();
    }
}
