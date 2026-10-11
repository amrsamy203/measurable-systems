package com.amrsamy.mep.thermiq.service;

import com.amrsamy.mep.thermiq.config.PlanCatalog;
import com.amrsamy.mep.thermiq.model.Plan;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PlanPricing {

    public static final int MIN_UNITS = 1;
    public static final int MAX_UNITS = 20;
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);

    private final PlanCatalog catalog;

    public PlanPricing(PlanCatalog catalog) {
        this.catalog = catalog;
    }

    public List<PlanQuote> quoteAll(Billing billing, int units) {
        return catalog.plans().stream().map(plan -> quote(plan, billing, units)).toList();
    }

    public PlanQuote quote(Plan plan, Billing billing, int requestedUnits) {
        int units = clampUnits(requestedUnits);
        BigDecimal monthly = plan.monthlyBase()
                .add(plan.extraUnitMonthly().multiply(BigDecimal.valueOf(units - 1L)));
        if (billing == Billing.MONTHLY) {
            return new PlanQuote(plan, billing, units, monthly, monthly, BigDecimal.ZERO);
        }
        BigDecimal fullYear = monthly.multiply(TWELVE);
        BigDecimal yearly = fullYear.multiply(BigDecimal.ONE.subtract(catalog.yearlyDiscount()))
                .setScale(0, RoundingMode.HALF_UP);
        return new PlanQuote(plan, billing, units, yearly.divide(TWELVE, 0, RoundingMode.HALF_UP), yearly,
                fullYear.subtract(yearly));
    }

    public static int clampUnits(int units) {
        return Math.max(MIN_UNITS, Math.min(MAX_UNITS, units));
    }

    public int yearlyDiscountPercent() {
        return catalog.yearlyDiscount().movePointRight(2).intValue();
    }
}
