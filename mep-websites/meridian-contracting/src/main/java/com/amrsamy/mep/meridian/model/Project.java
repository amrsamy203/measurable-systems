package com.amrsamy.mep.meridian.model;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public record Project(
        String slug,
        String name,
        Sector sector,
        String location,
        int year,
        Status status,
        int areaSqm,
        String client,
        List<String> disciplines,
        String image,
        boolean featured,
        String summary,
        String challenge,
        List<String> approach,
        List<String> outcomes,
        List<Stat> stats) {

    public enum Status {
        COMPLETED("Completed"),
        IN_PROGRESS("In progress");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public record Stat(String value, String label) {}

    public Project {
        disciplines = List.copyOf(disciplines);
        approach = List.copyOf(approach);
        outcomes = List.copyOf(outcomes);
        stats = List.copyOf(stats);
    }

    public String areaLabel() {
        return NumberFormat.getIntegerInstance(Locale.US).format(areaSqm) + " m²";
    }

    public boolean covers(String discipline) {
        return disciplines.contains(discipline);
    }
}
