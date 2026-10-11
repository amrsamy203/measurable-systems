package com.amrsamy.mep.thermiq.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.amrsamy.mep.common.inquiry.Inquiry;
import com.amrsamy.mep.common.inquiry.InquiryNotifier;
import com.amrsamy.mep.thermiq.config.BookingPolicy;
import com.amrsamy.mep.thermiq.model.Booking;
import com.amrsamy.mep.thermiq.model.ServiceArea;
import com.amrsamy.mep.thermiq.model.ServiceType;
import com.amrsamy.mep.thermiq.model.TimeSlot;
import com.amrsamy.mep.thermiq.repository.BookingRepository;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/** Runs the booking rules against a real (in-memory) database so the slot counting query is covered too. */
@DataJpaTest
@Import({BookingService.class, BookingServiceTest.Fixtures.class})
class BookingServiceTest {

    static final ZoneId CAIRO = ZoneId.of("Africa/Cairo");
    /** Sunday 11 October 2026, 08:00 in Cairo. */
    static final ZonedDateTime NOW = ZonedDateTime.of(2026, 10, 11, 8, 0, 0, 0, CAIRO);
    static final LocalDate TODAY = NOW.toLocalDate();
    static final LocalDate FRIDAY = LocalDate.of(2026, 10, 16);

    @TestConfiguration
    static class Fixtures {
        @Bean
        Clock clock() {
            return Clock.fixed(NOW.toInstant(), ZoneId.of("UTC"));
        }

        @Bean
        BookingPolicy bookingPolicy() {
            return new BookingPolicy(3, 30, Set.of(DayOfWeek.FRIDAY), 2, CAIRO);
        }
    }

    @Autowired
    BookingService service;

    @Autowired
    BookingRepository repository;

    @MockBean
    InquiryNotifier notifier;

    @Test
    void storesTheBookingAndAssignsAReferenceFromItsId() {
        Booking booking = service.book(request(TODAY.plusDays(1), TimeSlot.MORNING));

        assertThat(booking.getReference()).isEqualTo("TQ-2026-%06d".formatted(booking.getId()));
        assertThat(repository.findById(booking.getId())).get()
                .extracting(Booking::getCustomerName).isEqualTo("Laila Hassan");
        verify(notifier).send(any(Inquiry.class));
    }

    @Test
    void rejectsPastDatesDatesTooFarAheadAndClosedDays() {
        assertRejected(request(TODAY.minusDays(1), TimeSlot.MIDDAY), "date", "today or a later date");
        assertRejected(request(TODAY.plusDays(31), TimeSlot.MIDDAY), "date", "30 days ahead");
        assertRejected(request(FRIDAY, TimeSlot.MIDDAY), "date", "don't schedule visits");
    }

    @Test
    void sameDaySlotsNeedEnoughLeadTime() {
        // 09:00 is less than two hours away at 08:00; 12:00 is fine.
        assertRejected(request(TODAY, TimeSlot.MORNING), "slot", "too soon");
        assertThat(service.book(request(TODAY, TimeSlot.MIDDAY)).getReference()).isNotNull();
    }

    @Test
    void aSlotTakesOnlyAsManyBookingsAsThereAreCrews() {
        LocalDate monday = TODAY.plusDays(1);
        for (int i = 0; i < 3; i++) {
            service.book(request(monday, TimeSlot.AFTERNOON));
        }
        assertRejected(request(monday, TimeSlot.AFTERNOON), "slot", "filled up");
        assertThat(service.book(request(monday, TimeSlot.MIDDAY))).isNotNull();
    }

    @Test
    void availabilitySkipsClosedDaysAndCountsRemainingCrews() {
        service.book(request(TODAY.plusDays(1), TimeSlot.MIDDAY));
        service.book(request(TODAY.plusDays(1), TimeSlot.MIDDAY));

        List<DayAvailability> days = service.availability(6);

        assertThat(days).extracting(DayAvailability::date)
                .containsExactly(TODAY, TODAY.plusDays(1), TODAY.plusDays(2), TODAY.plusDays(3), TODAY.plusDays(4),
                        TODAY.plusDays(6));
        DayAvailability today = days.get(0);
        assertThat(today.slots().get(0)).isEqualTo(new DayAvailability.Slot(TimeSlot.MORNING, 3, false));
        assertThat(today.slots().get(1).open()).isTrue();
        assertThat(days.get(1).slots().get(1)).isEqualTo(new DayAvailability.Slot(TimeSlot.MIDDAY, 1, true));
    }

    @Test
    void nextOpenSlotIsTheFirstReachableOne() {
        assertThat(service.nextOpenSlot()).contains(new BookingService.NextSlot(TODAY, TimeSlot.MIDDAY, true));
    }

    private void assertRejected(BookingRequest request, String field, String message) {
        assertThatThrownBy(() -> service.book(request))
                .isInstanceOf(BookingRejectedException.class)
                .hasMessageContaining(message)
                .extracting(e -> ((BookingRejectedException) e).getField()).isEqualTo(field);
    }

    private static BookingRequest request(LocalDate date, TimeSlot slot) {
        return new BookingRequest(ServiceType.MAINTENANCE, 2, ServiceArea.MAADI, " 12 Road 233, flat 4 ", date, slot,
                "Laila Hassan", "010 1234 5678", "", null);
    }
}
