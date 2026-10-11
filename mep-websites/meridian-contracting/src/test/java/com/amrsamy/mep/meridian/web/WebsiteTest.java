package com.amrsamy.mep.meridian.web;

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

import com.amrsamy.mep.common.inquiry.Inquiry;
import com.amrsamy.mep.common.inquiry.InquiryNotifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class WebsiteTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    InquiryNotifier notifier;

    @ParameterizedTest
    @ValueSource(strings = {"/", "/about", "/services", "/services/hvac", "/services/bms", "/projects",
            "/projects?sector=healthcare", "/projects?sector=nonsense", "/projects/nile-crest-medical-center",
            "/projects/smart-village-data-hub", "/contact", "/contact?service=fire-protection&sector=education"})
    void everyPageRenders(String url) throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Meridian MEP Contracting")))
                .andExpect(content().string(containsString("fictional company")));
    }

    @Test
    void sectorFilterOnlyListsThatSector() throws Exception {
        mvc.perform(get("/projects").param("sector", "hospitality"))
                .andExpect(content().string(containsString("Marsa Azure Resort")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("Delta Foods Processing Plant"))));
    }

    @Test
    void unknownProjectAndServiceAre404() throws Exception {
        mvc.perform(get("/projects/does-not-exist")).andExpect(status().isNotFound());
        mvc.perform(get("/services/does-not-exist")).andExpect(status().isNotFound());
    }

    @Test
    void invalidQuoteIsRedisplayedWithErrors() throws Exception {
        mvc.perform(post("/contact").param("fullName", "").param("email", "not-an-email").param("phone", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact"))
                .andExpect(model().attributeHasFieldErrors("quote",
                        "fullName", "email", "phone", "sector", "stage", "disciplines", "location", "message", "consent"))
                .andExpect(content().string(containsString("Enter a valid email address")));
        verify(notifier, never()).send(any());
    }

    @Test
    void unknownDisciplineIsRejected() throws Exception {
        mvc.perform(validQuote().param("disciplines", "teleportation"))
                .andExpect(view().name("contact"))
                .andExpect(model().attributeHasFieldErrorCode("quote", "disciplines", "unknown"));
    }

    @Test
    void validQuoteRedirectsWithReferenceAndNotifies() throws Exception {
        mvc.perform(validQuote())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact/thank-you"))
                .andExpect(flash().attribute("reference", matchesPattern("MER-RFQ-\\d{4}-\\d{4}")));
        verify(notifier).send(any(Inquiry.class));
    }

    @Test
    void honeypotSubmissionLooksSuccessfulButIsNotDelivered() throws Exception {
        mvc.perform(validQuote().param("website", "http://spam.example"))
                .andExpect(redirectedUrl("/contact/thank-you"));
        verify(notifier, never()).send(any());
    }

    @Test
    void thankYouPageNeedsASubmission() throws Exception {
        mvc.perform(get("/contact/thank-you")).andExpect(redirectedUrl("/contact"));
    }

    private MockHttpServletRequestBuilder validQuote() {
        return post("/contact")
                .param("fullName", "Mona Adel")
                .param("company", "Adel Developments")
                .param("email", "mona@example.com")
                .param("phone", "010 1234 5678")
                .param("sector", "HEALTHCARE")
                .param("stage", "TENDER")
                .param("disciplines", "hvac", "electrical")
                .param("location", "New Cairo")
                .param("builtUpArea", "12000")
                .param("message", "A 120-bed hospital extension; we have tender drawings and a BOQ ready.")
                .param("consent", "true");
    }
}
