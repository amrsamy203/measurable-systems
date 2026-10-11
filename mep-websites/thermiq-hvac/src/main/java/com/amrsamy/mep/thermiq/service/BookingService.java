package com.amrsamy.mep.thermiq.service;

import com.amrsamy.mep.common.inquiry.Inquiry;
import com.amrsamy.mep.common.inquiry.InquiryNotifier;
import com.amrsamy.mep.thermiq.config.BookingPolicy;
import com.amrsamy.mep.thermiq.model.Booking;
import com.amrsamy.mep.thermiq.model.TimeSlot;
import com.amrsamy.mep.thermiq.repository.BookingRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookings;
    private final BookingPolicy policy;
    private final InquiryNotifier notifier;
    private final Clock clock;

    public BookingService(BookingRepository bookings, BookingPolicy policy, InquiryNotifier notifier, Clock clock) {
        this.bookings = bookings;
        this.policy = policy;
        this.notifier = notifier;
        this.clock = clock;
    }

    /**
     * Checks the business rules, stores the booking and returns it with its reference.
     *
     * @throws BookingRejectedException if the date or slot can't be booked
     */
    @Transactional
    public Booking book(BookingRequest request) {
        checkDate(request.date());
        checkSlot(request.date(), request.slot());

        Booking booking = bookings.save(new Booking(request.service(), request.units(), request.area(),
                request.address().strip(), request.date(), request.slot(), request.customerName().strip(),
                request.phone().strip(), blankToNull(request.email()), blankToNull(request.notes()), clock.instant()));
        booking.assignReference("TQ-%d-%06d".formatted(now().getYear(), booking.getId()));

        notifier.send(new Inquiry(booking.getReference(), "Service booking", booking.getCustomerName(),
                booking.getEmail(), booking.getPhone(), details(booking), booking.getCreatedAt()));
        return booking;
    }

    /** Open days from today, skipping closed days, with what is left in each slot. */
    @Transactional(readOnly = true)
    public List<DayAvailability> availability(int openDays) {
        LocalDate today = now().toLocalDate();
        List<LocalDate> days = new ArrayList<>();
        for (LocalDate d = today; days.size() < openDays && !d.isAfter(lastBookableDay()); d = d.plusDays(1)) {
            if (!policy.closedDays().contains(d.getDayOfWeek())) {
                days.add(d);
            }
        }
        if (days.isEmpty()) {
            return List.of();
        }

        Map<LocalDate, Map<TimeSlot, Long>> taken = new HashMap<>();
        for (BookingRepository.SlotCount count : bookings.countPerSlot(days.get(0), days.get(days.size() - 1))) {
            taken.computeIfAbsent(count.getDate(), d -> new HashMap<>()).put(count.getSlot(), count.getTotal());
        }

        return days.stream().map(day -> new DayAvailability(day, Arrays.stream(TimeSlot.values())
                .map(slot -> {
                    int remaining = (int) Math.max(0, policy.slotCapacity()
                            - taken.getOrDefault(day, Map.of()).getOrDefault(slot, 0L));
                    return new DayAvailability.Slot(slot, remaining, remaining > 0 && reachable(day, slot));
                })
                .toList())).toList();
    }

    /** The earliest bookable visit within the next week, for the "next free slot" badge. */
    @Transactional(readOnly = true)
    public Optional<NextSlot> nextOpenSlot() {
        return availability(6).stream()
                .flatMap(day -> day.slots().stream()
                        .filter(DayAvailability.Slot::open)
                        .map(s -> new NextSlot(day.date(), s.slot(), day.date().equals(now().toLocalDate()))))
                .findFirst();
    }

    public record NextSlot(LocalDate date, TimeSlot slot, boolean today) {}

    private void checkDate(LocalDate date) {
        LocalDate today = now().toLocalDate();
        if (date.isBefore(today)) {
            throw new BookingRejectedException("date", "Please choose today or a later date");
        }
        if (date.isAfter(lastBookableDay())) {
            throw new BookingRejectedException("date",
                    "We take bookings up to %d days ahead".formatted(policy.horizonDays()));
        }
        if (policy.closedDays().contains(date.getDayOfWeek())) {
            throw new BookingRejectedException("date", "We don't schedule visits on that day — please pick another");
        }
    }

    private void checkSlot(LocalDate date, TimeSlot slot) {
        if (!reachable(date, slot)) {
            throw new BookingRejectedException("slot", "That slot starts too soon for a crew to reach you — choose a later one");
        }
        if (bookings.countByDateAndSlot(date, slot) >= policy.slotCapacity()) {
            throw new BookingRejectedException("slot", "That slot has just filled up — please choose another");
        }
    }

    private boolean reachable(LocalDate date, TimeSlot slot) {
        ZonedDateTime start = date.atTime(slot.getStart()).atZone(policy.zone());
        return !start.isBefore(now().plusHours(policy.minLeadHours()));
    }

    private LocalDate lastBookableDay() {
        return now().toLocalDate().plusDays(policy.horizonDays());
    }

    private ZonedDateTime now() {
        return ZonedDateTime.now(clock.withZone(policy.zone()));
    }

    private static Map<String, String> details(Booking b) {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Service", b.getService().getLabel());
        details.put("Units", String.valueOf(b.getUnits()));
        details.put("Visit", b.getDate() + " " + b.getSlot().getLabel());
        details.put("Area", b.getArea().getLabel());
        details.put("Address", b.getAddress());
        details.put("Notes", b.getNotes() == null ? "-" : b.getNotes());
        return details;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
