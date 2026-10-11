package com.amrsamy.mep.thermiq.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.amrsamy.mep.common.inquiry.InquiryNotifier;
import com.amrsamy.mep.thermiq.repository.BookingRepository;
import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:thermiq-web;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@Transactional
class WebsiteTest {

    /** Sunday 11 October 2026, 08:00 in Cairo; Monday the 12th is open, Friday the 16th is closed. */
    @TestConfiguration
    static class FixedClock {
        @Bean
        Clock clock() {
            return Clock.fixed(ZonedDateTime.of(2026, 10, 11, 8, 0, 0, 0, ZoneId.of("Africa/Cairo")).toInstant(),
                    ZoneId.of("UTC"));
        }
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    BookingRepository bookings;

    @MockBean
    InquiryNotifier notifier;

    @ParameterizedTest
    @ValueSource(strings = {"/", "/services", "/plans", "/plans?billing=yearly&units=3", "/plans?units=lots",
            "/calculator", "/book", "/book?service=repair&date=2026-10-12&slot=MIDDAY&units=2", "/about", "/contact"})
    void everyPageRenders(String url) throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Thermiq HVAC Services")))
                .andExpect(content().string(containsString("fictional company")));
    }

    @Test
    void homeShowsTheNextFreeSlot() throws Exception {
        mvc.perform(get("/"))
                .andExpect(content().string(allOf(containsString("Next free slot"), containsString("12:00 – 15:00"))));
    }

    @Test
    void yearlyPricesAreCalculatedOnTheServer() throws Exception {
        mvc.perform(get("/plans").param("billing", "yearly").param("units", "3"))
                .andExpect(content().string(allOf(containsString("5,579"), containsString("465"), containsString("save"))));
    }

    @Test
    void calculatorRecommendsASize() throws Exception {
        mvc.perform(post("/calculator").param("area", "20").param("room", "LIVING").param("sun", "NORMAL").param("occupants", "2"))
                .andExpect(model().attributeExists("result"))
                .andExpect(content().string(allOf(containsString("2.25 HP"), containsString("13,000"))));
    }

    @Test
    void calculatorValidatesInput() throws Exception {
        mvc.perform(post("/calculator").param("area", "2").param("occupants", ""))
                .andExpect(model().attributeHasFieldErrors("calc", "area", "occupants"))
                .andExpect(model().attributeDoesNotExist("result"))
                .andExpect(content().string(containsString("Rooms start at 5 m²")));
    }

    @Test
    void validBookingIsStoredAndConfirmed() throws Exception {
        var result = mvc.perform(validBooking())
                .andExpect(redirectedUrl("/book/confirmed"))
                .andReturn();
        BookingConfirmation confirmation = (BookingConfirmation) result.getFlashMap().get("confirmation");
        assertThat(confirmation.reference()).matches("TQ-2026-\\d{6}");
        assertThat(bookings.count()).isEqualTo(1);
        verify(notifier).send(any());

        mvc.perform(get("/book/confirmed").flashAttrs(result.getFlashMap()))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(containsString(confirmation.reference()),
                        containsString("Laila Hassan"), containsString("Monday 12 October 2026"))));
    }

    @Test
    void bookingNeedsAMobileNumberAndRequiredFields() throws Exception {
        mvc.perform(post("/book").param("phone", "02 2500 0190").param("units", "0"))
                .andExpect(view().name("book"))
                .andExpect(model().attributeHasFieldErrors("booking",
                        "service", "units", "area", "address", "date", "slot", "fullName", "phone"))
                .andExpect(content().string(containsString("Enter an Egyptian mobile number")));
        assertThat(bookings.count()).isZero();
    }

    @Test
    void businessRulesShowUpAsFieldErrors() throws Exception {
        mvc.perform(bookingOn("2026-10-16"))
                .andExpect(view().name("book"))
                .andExpect(model().attributeHasFieldErrorCode("booking", "date", "booking"))
                .andExpect(content().string(containsString("schedule visits on that day")));
        assertThat(bookings.count()).isZero();
    }

    @Test
    void honeypotBookingIsNotStored() throws Exception {
        mvc.perform(validBooking().param("website", "spam"))
                .andExpect(redirectedUrl("/book/confirmed"));
        assertThat(bookings.count()).isZero();
        verify(notifier, never()).send(any());
    }

    @Test
    void confirmationPageNeedsABooking() throws Exception {
        mvc.perform(get("/book/confirmed")).andExpect(redirectedUrl("/book"));
    }

    @Test
    void contactFormValidatesAndSends() throws Exception {
        mvc.perform(post("/contact").param("fullName", "").param("message", "hi"))
                .andExpect(view().name("contact"))
                .andExpect(model().attributeHasFieldErrors("message", "fullName", "phone", "topic", "message"));

        mvc.perform(post("/contact")
                        .param("fullName", "Rania Fawzy").param("phone", "0100 555 0101").param("topic", "COMMERCIAL")
                        .param("message", "We have 18 units across two clinics and need a monthly contract."))
                .andExpect(redirectedUrl("/contact/thank-you"))
                .andExpect(flash().attribute("reference", matchesPattern("TQ-MSG-\\d{4}-\\d{4}")));
        verify(notifier).send(any());
    }

    private MockHttpServletRequestBuilder validBooking() {
        return bookingOn("2026-10-12");
    }

    private MockHttpServletRequestBuilder bookingOn(String date) {
        return post("/book")
                .param("service", "MAINTENANCE")
                .param("units", "2")
                .param("area", "MAADI")
                .param("address", "12 Road 233, flat 4")
                .param("date", date)
                .param("slot", "MIDDAY")
                .param("fullName", "Laila Hassan")
                .param("phone", "010 1234 5678");
    }
}
