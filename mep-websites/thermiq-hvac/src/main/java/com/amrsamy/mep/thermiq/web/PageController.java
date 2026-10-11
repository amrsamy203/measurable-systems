package com.amrsamy.mep.thermiq.web;

import com.amrsamy.mep.thermiq.model.ServiceArea;
import com.amrsamy.mep.thermiq.model.ServiceType;
import com.amrsamy.mep.thermiq.service.Billing;
import com.amrsamy.mep.thermiq.service.BookingService;
import com.amrsamy.mep.thermiq.service.PlanPricing;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final PlanPricing pricing;
    private final BookingService bookings;

    public PageController(PlanPricing pricing, BookingService bookings) {
        this.pricing = pricing;
        this.bookings = bookings;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("services", ServiceType.values());
        model.addAttribute("areas", ServiceArea.values());
        model.addAttribute("quotes", pricing.quoteAll(Billing.MONTHLY, 1));
        model.addAttribute("nextSlot", bookings.nextOpenSlot().orElse(null));
        return "home";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("services", ServiceType.values());
        return "services";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("areas", ServiceArea.values());
        return "about";
    }
}
