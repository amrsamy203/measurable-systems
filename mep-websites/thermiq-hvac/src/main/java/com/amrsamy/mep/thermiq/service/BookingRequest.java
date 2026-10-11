package com.amrsamy.mep.thermiq.service;

import com.amrsamy.mep.thermiq.model.ServiceArea;
import com.amrsamy.mep.thermiq.model.ServiceType;
import com.amrsamy.mep.thermiq.model.TimeSlot;
import java.time.LocalDate;

/** Already validated for shape by the web layer; {@link BookingService} applies the business rules. */
public record BookingRequest(
        ServiceType service,
        int units,
        ServiceArea area,
        String address,
        LocalDate date,
        TimeSlot slot,
        String customerName,
        String phone,
        String email,
        String notes) {}
