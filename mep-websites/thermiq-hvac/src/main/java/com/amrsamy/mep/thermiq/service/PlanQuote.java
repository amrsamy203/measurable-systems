package com.amrsamy.mep.thermiq.service;

import com.amrsamy.mep.thermiq.model.Plan;
import java.math.BigDecimal;

/**
 * @param perMonth   the effective monthly price shown in large type
 * @param billed     what is actually charged per billing period
 * @param saving     yearly saving compared with paying monthly; zero for monthly billing
 */
public record PlanQuote(Plan plan, Billing billing, int units, BigDecimal perMonth, BigDecimal billed, BigDecimal saving) {}
