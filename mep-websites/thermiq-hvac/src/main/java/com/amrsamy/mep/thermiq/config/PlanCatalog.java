package com.amrsamy.mep.thermiq.config;

import com.amrsamy.mep.thermiq.model.Plan;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** Maintenance plans and prices, kept in configuration so they can change without a release. */
@Validated
@ConfigurationProperties(prefix = "thermiq.pricing")
public record PlanCatalog(
        @DecimalMin("0") @DecimalMax("0.5") BigDecimal yearlyDiscount,
        @NotEmpty List<Plan> plans) {}
