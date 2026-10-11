package com.amrsamy.mep.thermiq.web;

import com.amrsamy.mep.thermiq.model.Booking;
import java.io.Serializable;
import java.time.LocalDate;

/** What the confirmation page shows; carried across the redirect as a flash attribute. */
public record BookingConfirmation(String reference, String name, String service, int units, LocalDate date,
                                  String slot, String area) implements Serializable {

    static BookingConfirmation of(Booking booking) {
        return new BookingConfirmation(booking.getReference(), booking.getCustomerName(),
                booking.getService().getLabel(), booking.getUnits(), booking.getDate(),
                booking.getSlot().getLabel(), booking.getArea().getLabel());
    }
}
