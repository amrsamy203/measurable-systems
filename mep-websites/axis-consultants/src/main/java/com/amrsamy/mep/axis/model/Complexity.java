package com.amrsamy.mep.axis.model;

public enum Complexity {
    STANDARD("Standard", "Offices, residential, retail shells"),
    ENHANCED("Enhanced", "Hotels, schools, malls, labs-light"),
    CRITICAL("Critical", "Hospitals, laboratories, data centres");

    private final String label;
    private final String examples;

    Complexity(String label, String examples) {
        this.label = label;
        this.examples = examples;
    }

    public String getLabel() {
        return label;
    }

    public String getExamples() {
        return examples;
    }
}
