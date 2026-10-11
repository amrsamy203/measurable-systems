package com.amrsamy.mep.thermiq.web;

import com.amrsamy.mep.thermiq.service.ContactService;
import com.amrsamy.mep.thermiq.web.form.ContactForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contact;

    public ContactController(ContactService contact) {
        this.contact = contact;
    }

    @ModelAttribute("topics")
    public ContactForm.Topic[] topics() {
        return ContactForm.Topic.values();
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("message", new ContactForm());
        return "contact";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("message") ContactForm form, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "contact";
        }
        redirect.addFlashAttribute("reference", contact.submit(form));
        redirect.addFlashAttribute("name", form.getFullName());
        return "redirect:/contact/thank-you";
    }

    @GetMapping("/thank-you")
    public String thankYou(Model model) {
        return model.containsAttribute("reference") ? "contact-thanks" : "redirect:/contact";
    }
}
