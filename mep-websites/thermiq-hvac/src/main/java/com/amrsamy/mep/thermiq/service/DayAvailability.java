package com.amrsamy.mep.thermiq.service;

import com.amrsamy.mep.thermiq.model.TimeSlot;
import java.time.LocalDate;
import java.util.List;

public record DayAvailability(LocalDate date, List<Slot> slots) {

    /** @param open false when the slot is full or starts too soon to reach */
    public record Slot(TimeSlot slot, int remaining, boolean open) {}

    public boolean hasOpenSlot() {
        return slots.stream().anyMatch(Slot::open);
    }
}
