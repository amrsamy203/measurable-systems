package com.amrsamy.mep.common.inquiry;

import java.time.Clock;
import java.time.Year;
import java.util.concurrent.atomic.AtomicInteger;

/** Human-friendly, sequential references such as {@code MER-RFQ-2026-0042}. */
public class ReferenceGenerator {

    private final String prefix;
    private final Clock clock;
    private final AtomicInteger sequence = new AtomicInteger();

    public ReferenceGenerator(String prefix, Clock clock) {
        this.prefix = prefix;
        this.clock = clock;
    }

    public String next() {
        return "%s-%d-%04d".formatted(prefix, Year.now(clock).getValue(), sequence.incrementAndGet());
    }
}
