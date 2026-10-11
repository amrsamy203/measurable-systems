package com.amrsamy.mep.meridian.service;

import com.amrsamy.mep.common.inquiry.Inquiry;
import com.amrsamy.mep.common.inquiry.InquiryNotifier;
import com.amrsamy.mep.common.inquiry.ReferenceGenerator;
import com.amrsamy.mep.meridian.web.form.QuoteRequestForm;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {

    private final ReferenceGenerator references;
    private final InquiryNotifier notifier;
    private final ServiceCatalogService services;
    private final Clock clock;

    public QuoteService(ReferenceGenerator references, InquiryNotifier notifier,
                        ServiceCatalogService services, Clock clock) {
        this.references = references;
        this.notifier = notifier;
        this.services = services;
        this.clock = clock;
    }

    /**
     * Registers a quotation request and returns its reference. Honeypot hits get a reference
     * too, so bots can't tell they were filtered, but nobody is notified.
     */
    public String submit(QuoteRequestForm form) {
        String reference = references.next();
        if (form.isLikelySpam()) {
            return reference;
        }
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Company", blankToDash(form.getCompany()));
        details.put("Sector", form.getSector().getLabel());
        details.put("Stage", form.getStage().getLabel());
        details.put("Disciplines", String.join(", ", services.namesFor(form.getDisciplines())));
        details.put("Location", form.getLocation());
        details.put("Built-up area", form.getBuiltUpArea() == null ? "-" : form.getBuiltUpArea() + " m²");
        details.put("Message", form.getMessage());

        notifier.send(new Inquiry(reference, "Quotation request", form.getFullName(), form.getEmail(),
                form.getPhone(), details, clock.instant()));
        return reference;
    }

    private static String blankToDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
