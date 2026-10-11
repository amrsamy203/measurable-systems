package com.amrsamy.mep.meridian.web;

import com.amrsamy.mep.meridian.model.Sector;
import com.amrsamy.mep.meridian.model.ServiceLine;
import com.amrsamy.mep.meridian.service.QuoteService;
import com.amrsamy.mep.meridian.service.ServiceCatalogService;
import com.amrsamy.mep.meridian.web.form.QuoteRequestForm;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private final QuoteService quotes;
    private final ServiceCatalogService services;

    public ContactController(QuoteService quotes, ServiceCatalogService services) {
        this.quotes = quotes;
        this.services = services;
    }

    @ModelAttribute("sectors")
    public Sector[] sectors() {
        return Sector.values();
    }

    @ModelAttribute("stages")
    public QuoteRequestForm.Stage[] stages() {
        return QuoteRequestForm.Stage.values();
    }

    @ModelAttribute("disciplineOptions")
    public List<ServiceLine> disciplineOptions() {
        return services.all();
    }

    /** {@code ?service=hvac} and {@code ?sector=healthcare} pre-fill the form from CTAs elsewhere. */
    @GetMapping
    public String form(@RequestParam(required = false) String service,
                       @RequestParam(required = false) String sector,
                       Model model) {
        QuoteRequestForm form = new QuoteRequestForm();
        if (service != null && services.bySlug().containsKey(service)) {
            form.setDisciplines(List.of(service));
        }
        Sector.fromSlug(sector).ifPresent(form::setSector);
        model.addAttribute("quote", form);
        return "contact";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("quote") QuoteRequestForm form, BindingResult result,
                         RedirectAttributes redirect) {
        Map<String, ServiceLine> known = services.bySlug();
        if (form.getDisciplines() != null && !known.keySet().containsAll(form.getDisciplines())) {
            result.rejectValue("disciplines", "unknown", "Select disciplines from the list");
        }
        if (result.hasErrors()) {
            return "contact";
        }
        redirect.addFlashAttribute("reference", quotes.submit(form));
        redirect.addFlashAttribute("name", form.getFullName());
        return "redirect:/contact/thank-you";
    }

    @GetMapping("/thank-you")
    public String thankYou(Model model) {
        // Only reachable straight after a submission (flash attributes survive one redirect).
        return model.containsAttribute("reference") ? "contact-thanks" : "redirect:/contact";
    }
}
