package com.amrsamy.mep.common.inquiry;

import java.time.Instant;
import java.util.Map;

/** A validated lead from any site form, in a shape notifiers can deliver without knowing the form. */
public record Inquiry(
        String reference,
        String type,
        String name,
        String email,
        String phone,
        Map<String, String> details,
        Instant receivedAt) {

    public Inquiry {
        details = details == null ? Map.of() : Map.copyOf(details);
    }
}
