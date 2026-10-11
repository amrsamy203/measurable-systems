package com.amrsamy.mep.axis.service;

import com.amrsamy.mep.axis.model.Discipline;
import com.amrsamy.mep.axis.web.form.BriefForm;
import com.amrsamy.mep.common.inquiry.Inquiry;
import com.amrsamy.mep.common.inquiry.InquiryNotifier;
import com.amrsamy.mep.common.inquiry.ReferenceGenerator;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BriefService {

    private final ReferenceGenerator references;
    private final InquiryNotifier notifier;
    private final Clock clock;

    public BriefService(ReferenceGenerator references, InquiryNotifier notifier, Clock clock) {
        this.references = references;
        this.notifier = notifier;
        this.clock = clock;
    }

    /** Returns the brief reference. Honeypot hits get one too, but nobody is notified. */
    public String submit(BriefForm form) {
        String reference = references.next();
        if (form.isLikelySpam()) {
            return reference;
        }
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Organisation", form.getOrganisation());
        details.put("Role", form.getRole().getLabel());
        details.put("Project", form.getProjectName());
        details.put("Sector", form.getSector().getLabel());
        details.put("Start from", form.getStartStage().getLabel());
        details.put("Disciplines", form.getDisciplines().stream().map(Discipline::getLabel).collect(Collectors.joining(", ")));
        details.put("Area", form.getArea() == null ? "-" : form.getArea() + " m²");
        details.put("Description", form.getDescription());

        notifier.send(new Inquiry(reference, "Project brief", form.getFullName(), form.getEmail(),
                form.getPhone(), details, clock.instant()));
        return reference;
    }
}
