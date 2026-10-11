package com.amrsamy.mep.axis.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @param effectiveArea area after economies of scale, which the per-m² rates apply to
 * @param lines         one line per discipline, then optional BIM / supervision lines
 * @param total         rounded to the nearest EGP 1,000 and never below the minimum fee
 * @param low           lower end of the indicative range shown to visitors
 * @param high          upper end of the indicative range
 */
public record FeeEstimate(
        int area,
        BigDecimal effectiveArea,
        List<Line> lines,
        BigDecimal total,
        BigDecimal low,
        BigDecimal high,
        boolean minimumApplied) {

    public record Line(String label, BigDecimal amount) {}
}
