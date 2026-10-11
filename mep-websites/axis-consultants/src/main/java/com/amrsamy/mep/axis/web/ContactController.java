package com.amrsamy.mep.axis.web;

import com.amrsamy.mep.axis.model.Discipline;
import com.amrsamy.mep.axis.model.Sector;
import com.amrsamy.mep.axis.model.Stage;
import com.amrsamy.mep.axis.service.BriefService;
import com.amrsamy.mep.axis.web.form.BriefForm;
import jakarta.validation.Valid;
import java.util.List;
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

    private final BriefService briefs;

    public ContactController(BriefService briefs) {
        this.briefs = briefs;
    }

    @ModelAttribute("sectors")
    public Sector[] sectors() {
        return Sector.values();
    }

    @ModelAttribute("stages")
    public Stage[] stages() {
        return Stage.values();
    }

    @ModelAttribute("roles")
    public BriefForm.ClientRole[] roles() {
        return BriefForm.ClientRole.values();
    }

    @ModelAttribute("disciplineOptions")
    public Discipline[] disciplineOptions() {
        return Discipline.values();
    }

    /** The fee estimator links here with {@code ?area=&disciplines=} so visitors don't retype them. */
    @GetMapping
    public String form(@RequestParam(required = false) Integer area,
                       @RequestParam(required = false) List<Discipline> disciplines,
                       Model model) {
        BriefForm form = new BriefForm();
        form.setArea(area);
        if (disciplines != null) {
            form.setDisciplines(disciplines);
        }
        model.addAttribute("brief", form);
        return "contact";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("brief") BriefForm form, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "contact";
        }
        redirect.addFlashAttribute("reference", briefs.submit(form));
        redirect.addFlashAttribute("name", form.getFullName());
        redirect.addFlashAttribute("projectName", form.getProjectName());
        return "redirect:/contact/thank-you";
    }

    @GetMapping("/thank-you")
    public String thankYou(Model model) {
        return model.containsAttribute("reference") ? "contact-thanks" : "redirect:/contact";
    }
}
