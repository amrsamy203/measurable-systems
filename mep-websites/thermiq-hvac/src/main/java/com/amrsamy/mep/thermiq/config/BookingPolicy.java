package com.amrsamy.mep.thermiq.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @param slotCapacity  crews available per time slot
 * @param horizonDays   how far ahead visits can be booked
 * @param closedDays    days with no visits
 * @param minLeadHours  a same-day slot must start at least this many hours from now
 * @param zone          the business time zone; "today" is evaluated here, not in the server's zone
 */
@Validated
@ConfigurationProperties(prefix = "thermiq.booking")
public record BookingPolicy(
        @Min(1) int slotCapacity,
        @Min(1) @Max(90) int horizonDays,
        @NotNull Set<DayOfWeek> closedDays,
        @Min(0) int minLeadHours,
        @NotNull ZoneId zone) {}
