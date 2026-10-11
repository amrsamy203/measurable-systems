package com.amrsamy.mep.thermiq.service;

import java.util.Arrays;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Rule-of-thumb AC sizing for Egyptian homes and small businesses. It is deliberately simple —
 * a technician confirms the size on site — but it stops people buying a 1.5 HP unit for a 30 m² room.
 */
@Service
public class CoolingLoadCalculator {

    static final int BTU_PER_SQM = 650;
    static final int BTU_PER_EXTRA_PERSON = 600;
    static final int PEOPLE_INCLUDED = 2;

    // Factors are whole percentages so the arithmetic stays exact.
    public enum RoomType {
        BEDROOM("Bedroom", 90),
        LIVING("Living / reception", 100),
        OFFICE("Office", 110),
        SHOP("Shop / clinic", 120),
        KITCHEN("Kitchen", 130);

        private final String label;
        private final int percent;

        RoomType(String label, int percent) {
            this.label = label;
            this.percent = percent;
        }

        public String getLabel() { return label; }
    }

    public enum SunExposure {
        LOW("Shaded / north-facing", 90),
        NORMAL("Normal", 100),
        HIGH("Strong sun / top floor / west-facing", 115);

        private final String label;
        private final int percent;

        SunExposure(String label, int percent) {
            this.label = label;
            this.percent = percent;
        }

        public String getLabel() { return label; }
    }

    /** Common split-unit sizes on the Egyptian market. */
    public enum UnitSize {
        HP_1_5("1.5 HP", 12_000),
        HP_2_25("2.25 HP", 18_000),
        HP_3("3 HP", 24_000),
        HP_4("4 HP", 30_000),
        HP_5("5 HP", 36_000);

        private final String label;
        private final int btu;

        UnitSize(String label, int btu) {
            this.label = label;
            this.btu = btu;
        }

        public String getLabel() { return label; }
        public int getBtu() { return btu; }
    }

    /**
     * @param size        the single unit that covers the load, or empty when one unit isn't enough
     * @param unitsNeeded how many {@code suggestedSize} units are needed
     */
    public record Recommendation(int btu, Optional<UnitSize> size, UnitSize suggestedSize, int unitsNeeded) {

        public boolean singleUnit() {
            return size.isPresent();
        }
    }

    public Recommendation recommend(int areaSqm, RoomType room, SunExposure sun, int occupants) {
        if (areaSqm <= 0 || occupants <= 0) {
            throw new IllegalArgumentException("Area and occupants must be positive");
        }
        long scaled = (long) areaSqm * BTU_PER_SQM * room.percent * sun.percent;
        long load = ceilDiv(scaled, 10_000)
                + (long) Math.max(0, occupants - PEOPLE_INCLUDED) * BTU_PER_EXTRA_PERSON;
        int btu = (int) (ceilDiv(load, 100) * 100);

        Optional<UnitSize> single = sizeFor(btu);
        if (single.isPresent()) {
            return new Recommendation(btu, single, single.get(), 1);
        }
        int units = (int) ceilDiv(btu, UnitSize.HP_3.btu);
        return new Recommendation(btu, Optional.empty(), UnitSize.HP_3, units);
    }

    static Optional<UnitSize> sizeFor(int btu) {
        return Arrays.stream(UnitSize.values()).filter(size -> size.btu >= btu).findFirst();
    }

    private static long ceilDiv(long value, long divisor) {
        return Math.ceilDiv(value, divisor);
    }
}
