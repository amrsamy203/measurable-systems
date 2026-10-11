package com.amrsamy.mep.axis.service;

import com.amrsamy.mep.axis.model.Complexity;
import com.amrsamy.mep.axis.model.Discipline;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class FeeEstimator {

    private static final BigDecimal THOUSAND = BigDecimal.valueOf(1000);
    private static final BigDecimal RANGE_LOW = new BigDecimal("0.90");
    private static final BigDecimal RANGE_HIGH = new BigDecimal("1.15");

    private final FeeSchedule schedule;

    public FeeEstimator(FeeSchedule schedule) {
        this.schedule = schedule;
    }

    public FeeEstimate estimate(int area, Complexity complexity, Set<Discipline> disciplines,
                                boolean bim, boolean supervision) {
        if (area <= 0) {
            throw new IllegalArgumentException("Area must be positive");
        }
        if (disciplines.isEmpty()) {
            throw new IllegalArgumentException("Select at least one discipline");
        }
        BigDecimal effectiveArea = effectiveArea(area);
        BigDecimal factor = schedule.complexityFactor().getOrDefault(complexity, BigDecimal.ONE);

        List<FeeEstimate.Line> lines = new ArrayList<>();
        BigDecimal designFee = BigDecimal.ZERO;
        // EnumSet keeps the lines in the same order as the site lists the disciplines.
        for (Discipline discipline : EnumSet.copyOf(disciplines)) {
            BigDecimal rate = schedule.ratePerSqm().get(discipline);
            if (rate == null) {
                throw new IllegalStateException("No fee rate configured for " + discipline);
            }
            BigDecimal amount = whole(effectiveArea.multiply(rate).multiply(factor));
            lines.add(new FeeEstimate.Line(discipline.getLabel(), amount));
            designFee = designFee.add(amount);
        }

        BigDecimal total = designFee;
        if (bim) {
            BigDecimal amount = whole(designFee.multiply(schedule.bimUplift()));
            lines.add(new FeeEstimate.Line("BIM coordination model (LOD 350)", amount));
            total = total.add(amount);
        }
        if (supervision) {
            BigDecimal amount = whole(designFee.multiply(schedule.supervisionUplift()));
            lines.add(new FeeEstimate.Line("Site supervision & handover", amount));
            total = total.add(amount);
        }

        total = roundToThousand(total);
        boolean minimumApplied = total.compareTo(schedule.minimumFee()) < 0;
        if (minimumApplied) {
            total = schedule.minimumFee();
        }
        return new FeeEstimate(area, effectiveArea, List.copyOf(lines), total,
                roundToThousand(total.multiply(RANGE_LOW)), roundToThousand(total.multiply(RANGE_HIGH)),
                minimumApplied);
    }

    /** Applies each area band's factor only to the slice of area that falls inside that band. */
    BigDecimal effectiveArea(int area) {
        BigDecimal effective = BigDecimal.ZERO;
        int lowerBound = 0;
        for (FeeSchedule.AreaBand band : schedule.areaBands()) {
            int upper = band.upTo() == null ? Integer.MAX_VALUE : band.upTo();
            int slice = Math.min(area, upper) - lowerBound;
            if (slice <= 0) {
                break;
            }
            effective = effective.add(band.factor().multiply(BigDecimal.valueOf(slice)));
            lowerBound = upper;
        }
        return effective.setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal whole(BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP);
    }

    private static BigDecimal roundToThousand(BigDecimal value) {
        return value.divide(THOUSAND, 0, RoundingMode.HALF_UP).multiply(THOUSAND);
    }
}
