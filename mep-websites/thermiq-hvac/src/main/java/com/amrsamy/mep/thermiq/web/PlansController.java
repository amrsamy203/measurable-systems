package com.amrsamy.mep.thermiq.web;

import com.amrsamy.mep.thermiq.service.Billing;
import com.amrsamy.mep.thermiq.service.PlanPricing;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlansController {

    private final PlanPricing pricing;

    public PlansController(PlanPricing pricing) {
        this.pricing = pricing;
    }

    /** Prices are calculated on the server from {@code ?billing=yearly&units=3}; the page works without JavaScript. */
    @GetMapping("/plans")
    public String plans(@RequestParam(required = false) String billing,
                        @RequestParam(defaultValue = "1") String units,
                        Model model) {
        Billing selected = Billing.fromParam(billing);
        int unitCount = PlanPricing.clampUnits(parseOrOne(units));
        model.addAttribute("billing", selected);
        model.addAttribute("units", unitCount);
        model.addAttribute("minUnits", PlanPricing.MIN_UNITS);
        model.addAttribute("maxUnits", PlanPricing.MAX_UNITS);
        model.addAttribute("discount", pricing.yearlyDiscountPercent());
        model.addAttribute("quotes", pricing.quoteAll(selected, unitCount));
        return "plans";
    }

    private static int parseOrOne(String value) {
        try {
            return Integer.parseInt(value.strip());
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
