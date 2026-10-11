package com.amrsamy.mep.thermiq.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * @param monthlyBase      monthly price covering one AC unit
 * @param extraUnitMonthly monthly price for each additional unit
 */
public record Plan(
        String code,
        String name,
        String tagline,
        BigDecimal monthlyBase,
        BigDecimal extraUnitMonthly,
        int visitsPerYear,
        String responseTime,
        int partsDiscount,
        List<String> features,
        boolean highlighted) {}
