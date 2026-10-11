package com.amrsamy.mep.common.inquiry;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ReferenceGeneratorTest {

    @Test
    void producesSequentialReferencesForTheCurrentYear() {
        Clock clock = Clock.fixed(Instant.parse("2026-03-15T10:00:00Z"), ZoneOffset.UTC);
        ReferenceGenerator generator = new ReferenceGenerator("MER-RFQ", clock);

        assertThat(generator.next()).isEqualTo("MER-RFQ-2026-0001");
        assertThat(generator.next()).isEqualTo("MER-RFQ-2026-0002");
    }
}
