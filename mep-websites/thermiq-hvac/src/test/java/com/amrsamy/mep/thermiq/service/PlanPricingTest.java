package com.amrsamy.mep.thermiq.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.amrsamy.mep.thermiq.config.PlanCatalog;
import com.amrsamy.mep.thermiq.model.Plan;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PlanPricingTest {

    private static final Plan COMFORT = new Plan("comfort", "Comfort", "", new BigDecimal("249"), new BigDecimal("149"),
            4, "24 hours", 10, List.of(), true);

    private final PlanPricing pricing = new PlanPricing(new PlanCatalog(new BigDecimal("0.15"), List.of(COMFORT)));

    @Test
    void monthlyPriceCoversOneUnitThenChargesPerExtraUnit() {
        assertThat(pricing.quote(COMFORT, Billing.MONTHLY, 1).perMonth()).isEqualByComparingTo("249");

        PlanQuote three = pricing.quote(COMFORT, Billing.MONTHLY, 3);
        assertThat(three.perMonth()).isEqualByComparingTo("547");
        assertThat(three.billed()).isEqualByComparingTo("547");
        assertThat(three.saving()).isEqualByComparingTo("0");
    }

    @Test
    void yearlyBillingAppliesTheDiscount() {
        PlanQuote quote = pricing.quote(COMFORT, Billing.YEARLY, 3);

        // 547 × 12 = 6,564; less 15% = 5,579.40 → 5,579 a year, about 465 a month.
        assertThat(quote.billed()).isEqualByComparingTo("5579");
        assertThat(quote.perMonth()).isEqualByComparingTo("465");
        assertThat(quote.saving()).isEqualByComparingTo("985");
    }

    @Test
    void unitsAreClampedToTheSupportedRange() {
        assertThat(pricing.quote(COMFORT, Billing.MONTHLY, 0).units()).isEqualTo(1);
        assertThat(pricing.quote(COMFORT, Billing.MONTHLY, 99).units()).isEqualTo(20);
    }

    @Test
    void billingParameterIsForgiving() {
        assertThat(Billing.fromParam("yearly")).isEqualTo(Billing.YEARLY);
        assertThat(Billing.fromParam("YEARLY")).isEqualTo(Billing.YEARLY);
        assertThat(Billing.fromParam("weekly")).isEqualTo(Billing.MONTHLY);
        assertThat(Billing.fromParam(null)).isEqualTo(Billing.MONTHLY);
    }

    @Test
    void discountIsShownAsAWholePercentage() {
        assertThat(pricing.yearlyDiscountPercent()).isEqualTo(15);
    }
}
