package com.amrsamy.mep.axis.model;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public record CaseStudy(
        String slug,
        String title,
        String client,
        String location,
        int year,
        Sector sector,
        List<Stage> stages,
        List<Discipline> disciplines,
        int areaSqm,
        String image,
        String caption,
        boolean featured,
        String headline,
        String brief,
        List<String> approach,
        List<Outcome> outcomes,
        List<String> tools) {

    public record Outcome(String value, String label) {}

    public String areaLabel() {
        return NumberFormat.getIntegerInstance(Locale.US).format(areaSqm) + " m²";
    }

    public boolean hasImage() {
        return image != null && !image.isBlank();
    }

    public boolean covers(Stage stage) {
        return stages.contains(stage);
    }

    public String disciplineCodes() {
        return String.join(" · ", disciplines.stream().map(Discipline::getCode).toList());
    }
}
