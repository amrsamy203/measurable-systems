package com.amrsamy.mep.thermiq.model;

import java.time.LocalTime;

public enum TimeSlot {
    MORNING(LocalTime.of(9, 0), LocalTime.of(12, 0)),
    MIDDAY(LocalTime.of(12, 0), LocalTime.of(15, 0)),
    AFTERNOON(LocalTime.of(15, 0), LocalTime.of(18, 0));

    private final LocalTime start;
    private final LocalTime end;

    TimeSlot(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public String getLabel() {
        return "%s – %s".formatted(start, end);
    }
}
