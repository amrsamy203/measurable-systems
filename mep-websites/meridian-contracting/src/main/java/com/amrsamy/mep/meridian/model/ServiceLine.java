package com.amrsamy.mep.meridian.model;

import java.util.List;

/** One MEP discipline Meridian delivers (named to avoid clashing with Spring's {@code @Service}). */
public record ServiceLine(
        String slug,
        String name,
        String icon,
        String summary,
        String description,
        List<String> scope,
        List<String> systems,
        List<String> standards,
        String image,
        String metricValue,
        String metricLabel) {

    public ServiceLine {
        scope = List.copyOf(scope);
        systems = List.copyOf(systems);
        standards = List.copyOf(standards);
    }
}
