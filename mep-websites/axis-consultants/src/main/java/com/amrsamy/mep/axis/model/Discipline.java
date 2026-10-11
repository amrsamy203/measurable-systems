package com.amrsamy.mep.axis.model;

public enum Discipline {
    MECHANICAL("M", "Mechanical & HVAC"),
    ELECTRICAL("E", "Electrical & lighting"),
    PUBLIC_HEALTH("P", "Plumbing & public health"),
    FIRE("F", "Fire & life safety"),
    ELV("L", "ELV & smart building"),
    SUSTAINABILITY("S", "Energy & sustainability");

    private final String code;
    private final String label;

    Discipline(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /** Used as the in-page anchor on /expertise. */
    public String getSlug() {
        return name().toLowerCase().replace('_', '-');
    }
}
