package com.amrsamy.mep.axis.service;

import com.amrsamy.mep.axis.model.Complexity;
import com.amrsamy.mep.axis.model.Discipline;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Commercial inputs for the fee estimator, bound from {@code axis.fees} so rates can change per
 * environment (or per year) without a code change.
 *
 * @param ratePerSqm        design fee in EGP per m² of effective area, per discipline
 * @param complexityFactor  multiplier for building complexity
 * @param areaBands         economies of scale: each band discounts the area that falls inside it;
 *                          the last band has no upper limit
 * @param bimUplift         share of the design fee added for a coordinated BIM model
 * @param supervisionUplift share of the design fee added for site supervision
 * @param minimumFee        floor for any commission
 */
@Validated
@ConfigurationProperties(prefix = "axis.fees")
public record FeeSchedule(
        @NotEmpty Map<Discipline, BigDecimal> ratePerSqm,
        @NotEmpty Map<Complexity, BigDecimal> complexityFactor,
        @NotEmpty @Valid List<AreaBand> areaBands,
        @NotNull BigDecimal bimUplift,
        @NotNull BigDecimal supervisionUplift,
        @NotNull @Positive BigDecimal minimumFee) {

    /** @param upTo cumulative upper bound in m², or {@code null} for the open-ended last band */
    public record AreaBand(Integer upTo, @NotNull BigDecimal factor) {}
}
