package com.amrsamy.mep.axis.web;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
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
    @ValueSource(strings = {"/", "/work", "/work?sector=healthcare", "/work?stage=concept&sector=workplace",
            "/work?sector=unknown", "/work/al-rawda-specialist-hospital", "/work/nile-university-research-labs",
            "/expertise", "/studio", "/fee-estimator", "/contact", "/contact?area=12000&disciplines=MECHANICAL"})
    void everyPageRenders(String url) throws Exception {
        mvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Axis Engineering Consultants")))
                .andExpect(content().string(containsString("fictional company")));
    }

    @Test
    void workFiltersCombine() throws Exception {
        mvc.perform(get("/work").param("sector", "workplace").param("stage", "supervision"))
                .andExpect(content().string(allOf(
                        containsString("New Capital Civic Headquarters"),
                        containsString("Zamalek Office Retrofit"),
                        not(containsString("Al Rawda Specialist Hospital")))));
    }

    @Test
    void unknownCaseStudyIs404() throws Exception {
        mvc.perform(get("/work/does-not-exist")).andExpect(status().isNotFound());
    }

    @Test
    void estimatorShowsTheFeeBreakdown() throws Exception {
        mvc.perform(post("/fee-estimator")
                        .param("area", "20000")
                        .param("complexity", "STANDARD")
                        .param("disciplines", "MECHANICAL", "ELECTRICAL"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("estimate"))
                .andExpect(content().string(allOf(
                        containsString("1,665,000"), containsString("2,128,000"), containsString("1,017,500"))));
    }

    @Test
    void estimatorValidatesOnTheServer() throws Exception {
        mvc.perform(post("/fee-estimator").param("area", "120"))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("estimate"))
                .andExpect(model().attributeHasFieldErrors("estimateForm", "area", "complexity", "disciplines"))
                .andExpect(content().string(containsString("The estimator starts at 500 m²")));
    }

    @Test
    void estimatorExplainsBadNumbersAndUnknownOptions() throws Exception {
        mvc.perform(post("/fee-estimator").param("area", "lots").param("complexity", "STANDARD")
                        .param("disciplines", "PLUMBING_MAGIC"))
                .andExpect(model().attributeHasFieldErrorCode("estimateForm", "area", "typeMismatch"))
                .andExpect(model().attributeHasFieldErrorCode("estimateForm", "disciplines", "typeMismatch"))
                .andExpect(content().string(containsString("Please enter a whole number")));
    }

    @Test
    void contactFormIsPrefilledFromTheEstimator() throws Exception {
        mvc.perform(get("/contact").param("area", "12000").param("disciplines", "MECHANICAL", "FIRE"))
                .andExpect(content().string(containsString("value=\"12000\"")))
                .andExpect(model().attribute("brief", org.hamcrest.Matchers.hasProperty("disciplines",
                        org.hamcrest.Matchers.hasSize(2))));
    }

    @Test
    void invalidBriefIsRedisplayedWithErrors() throws Exception {
        mvc.perform(post("/contact").param("email", "nope").param("phone", "12345"))
                .andExpect(view().name("contact"))
                .andExpect(model().attributeHasFieldErrors("brief", "fullName", "organisation", "role", "email",
                        "phone", "projectName", "sector", "startStage", "disciplines", "description", "consent"));
        verify(notifier, never()).send(any());
    }

    @Test
    void validBriefRedirectsWithReferenceAndNotifies() throws Exception {
        mvc.perform(validBrief())
                .andExpect(redirectedUrl("/contact/thank-you"))
                .andExpect(flash().attribute("reference", matchesPattern("AXS-BRF-\\d{4}-\\d{4}")))
                .andExpect(flash().attribute("projectName", "Heliopolis Clinic"));
        verify(notifier).send(any(Inquiry.class));
    }

    @Test
    void honeypotBriefIsSilentlyDropped() throws Exception {
        mvc.perform(validBrief().param("website", "spam"))
                .andExpect(redirectedUrl("/contact/thank-you"));
        verify(notifier, never()).send(any());
    }

    @Test
    void thankYouPageNeedsASubmission() throws Exception {
        mvc.perform(get("/contact/thank-you")).andExpect(redirectedUrl("/contact"));
    }

    private MockHttpServletRequestBuilder validBrief() {
        return post("/contact")
                .param("fullName", "Tarek Hamdy")
                .param("organisation", "Hamdy Architects")
                .param("role", "ARCHITECT")
                .param("email", "tarek@example.com")
                .param("projectName", "Heliopolis Clinic")
                .param("sector", "HEALTHCARE")
                .param("startStage", "SCHEMATIC")
                .param("disciplines", "MECHANICAL", "ELECTRICAL", "PUBLIC_HEALTH")
                .param("area", "6500")
                .param("description", "A four-storey day-surgery clinic; architectural schemes are approved.")
                .param("consent", "true");
    }
}
