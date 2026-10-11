package com.amrsamy.mep.thermiq.service;

/** A business rule refused the booking; {@code field} names the form field to show the message on. */
public class BookingRejectedException extends RuntimeException {

    private final String field;

    public BookingRejectedException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
