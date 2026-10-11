package com.amrsamy.mep.axis.service;

import static com.amrsamy.mep.axis.model.Discipline.ELECTRICAL;
import static com.amrsamy.mep.axis.model.Discipline.ELV;
import static com.amrsamy.mep.axis.model.Discipline.FIRE;
import static com.amrsamy.mep.axis.model.Discipline.MECHANICAL;
import static com.amrsamy.mep.axis.model.Discipline.PUBLIC_HEALTH;
import static com.amrsamy.mep.axis.model.Discipline.SUSTAINABILITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import com.amrsamy.mep.axis.model.Complexity;
import com.amrsamy.mep.axis.model.Discipline;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FeeEstimatorTest {

    private static final FeeSchedule SCHEDULE = new FeeSchedule(
            Map.of(MECHANICAL, bd("55"), ELECTRICAL, bd("45"), PUBLIC_HEALTH, bd("30"),
                    FIRE, bd("25"), ELV, bd("20"), SUSTAINABILITY, bd("15")),
            Map.of(Complexity.STANDARD, bd("1.0"), Complexity.ENHANCED, bd("1.2"), Complexity.CRITICAL, bd("1.5")),
            List.of(new FeeSchedule.AreaBand(10_000, bd("1.0")),
                    new FeeSchedule.AreaBand(50_000, bd("0.85")),
                    new FeeSchedule.AreaBand(null, bd("0.7"))),
            bd("0.18"), bd("0.30"), bd("120000"));

    private final FeeEstimator estimator = new FeeEstimator(SCHEDULE);

    @ParameterizedTest
    @CsvSource({"8000, 8000", "10000, 10000", "30000, 27000", "50000, 44000", "80000, 65000"})
    void areaBandsDiscountOnlyTheAreaInsideEachBand(int area, int expected) {
        assertThat(estimator.effectiveArea(area)).isEqualByComparingTo(bd(String.valueOf(expected)));
    }

    @Test
    void feeIsEffectiveAreaTimesRatePerDiscipline() {
        FeeEstimate estimate = estimator.estimate(20_000, Complexity.STANDARD, Set.of(MECHANICAL, ELECTRICAL), false, false);

        assertThat(estimate.effectiveArea()).isEqualByComparingTo("18500");
        assertThat(estimate.lines()).extracting(FeeEstimate.Line::amount)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactly(bd("1017500"), bd("832500"));
        assertThat(estimate.total()).isEqualByComparingTo("1850000");
        assertThat(estimate.low()).isEqualByComparingTo("1665000");
        assertThat(estimate.high()).isEqualByComparingTo("2128000");
        assertThat(estimate.minimumApplied()).isFalse();
    }

    @Test
    void complexityMultipliesTheRate() {
        FeeEstimate critical = estimator.estimate(10_000, Complexity.CRITICAL, Set.of(MECHANICAL), false, false);

        assertThat(critical.total()).isEqualByComparingTo("825000");
    }

    @Test
    void addOnsArePricedAsAShareOfTheDesignFee() {
        FeeEstimate estimate = estimator.estimate(10_000, Complexity.STANDARD, Set.of(MECHANICAL), true, true);

        assertThat(estimate.lines()).extracting(FeeEstimate.Line::label)
                .containsExactly("Mechanical & HVAC", "BIM coordination model (LOD 350)", "Site supervision & handover");
        assertThat(estimate.lines()).extracting(FeeEstimate.Line::amount)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactly(bd("550000"), bd("99000"), bd("165000"));
        assertThat(estimate.total()).isEqualByComparingTo("814000");
    }

    @Test
    void smallProjectsPayTheMinimumFee() {
        FeeEstimate estimate = estimator.estimate(600, Complexity.STANDARD, Set.of(ELV), false, false);

        assertThat(estimate.minimumApplied()).isTrue();
        assertThat(estimate.total()).isEqualByComparingTo("120000");
        assertThat(estimate.low()).isEqualByComparingTo("108000");
        assertThat(estimate.high()).isEqualByComparingTo("138000");
    }

    @Test
    void linesFollowTheSiteOrderOfDisciplines() {
        FeeEstimate estimate = estimator.estimate(5_000, Complexity.STANDARD, Set.of(FIRE, MECHANICAL, ELECTRICAL), false, false);

        assertThat(estimate.lines()).extracting(FeeEstimate.Line::label)
                .containsExactly("Mechanical & HVAC", "Electrical & lighting", "Fire & life safety");
    }

    @Test
    void rejectsImpossibleInputs() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> estimator.estimate(0, Complexity.STANDARD, Set.of(MECHANICAL), false, false));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> estimator.estimate(1_000, Complexity.STANDARD, Set.<Discipline>of(), false, false));
    }

    @Test
    void failsLoudlyWhenARateIsMissingFromConfiguration() {
        FeeSchedule incomplete = new FeeSchedule(Map.of(MECHANICAL, bd("55")), SCHEDULE.complexityFactor(),
                SCHEDULE.areaBands(), SCHEDULE.bimUplift(), SCHEDULE.supervisionUplift(), SCHEDULE.minimumFee());

        assertThatIllegalStateException()
                .isThrownBy(() -> new FeeEstimator(incomplete).estimate(1_000, Complexity.STANDARD, Set.of(ELV), false, false))
                .withMessageContaining("ELV");
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
