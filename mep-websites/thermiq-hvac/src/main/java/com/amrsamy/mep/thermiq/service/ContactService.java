package com.amrsamy.mep.thermiq.service;

import com.amrsamy.mep.common.inquiry.Inquiry;
import com.amrsamy.mep.common.inquiry.InquiryNotifier;
import com.amrsamy.mep.common.inquiry.ReferenceGenerator;
import com.amrsamy.mep.thermiq.web.form.ContactForm;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ReferenceGenerator references;
    private final InquiryNotifier notifier;
    private final Clock clock;

    public ContactService(ReferenceGenerator references, InquiryNotifier notifier, Clock clock) {
        this.references = references;
        this.notifier = notifier;
        this.clock = clock;
    }

    /** Returns the message reference. Honeypot hits get one too, but nobody is notified. */
    public String submit(ContactForm form) {
        String reference = references.next();
        if (form.isLikelySpam()) {
            return reference;
        }
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Topic", form.getTopic().getLabel());
        details.put("Message", form.getMessage());
        notifier.send(new Inquiry(reference, "Website message", form.getFullName(), form.getEmail(),
                form.getPhone(), details, clock.instant()));
        return reference;
    }
}
