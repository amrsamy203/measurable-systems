package com.amrsamy.mep.common.web;

import com.amrsamy.mep.common.CompanyProfile;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Year;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Exposes values every layout needs (Thymeleaf 3.1 no longer gives templates access to the request). */
@ControllerAdvice
public class SiteModelAdvice {

    private final CompanyProfile company;
    private final Clock clock;

    public SiteModelAdvice(CompanyProfile company, Clock clock) {
        this.company = company;
        this.clock = clock;
    }

    @ModelAttribute("company")
    public CompanyProfile company() {
        return company;
    }

    /** Request path without the servlet context path, e.g. {@code /projects/nile-crest}. */
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.isEmpty() ? "/" : path;
    }

    @ModelAttribute("year")
    public int year() {
        return Year.now(clock).getValue();
    }
}
