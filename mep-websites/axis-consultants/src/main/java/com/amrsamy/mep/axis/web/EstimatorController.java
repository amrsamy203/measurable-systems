package com.amrsamy.mep.axis.web;

import com.amrsamy.mep.axis.model.Complexity;
import com.amrsamy.mep.axis.model.Discipline;
import com.amrsamy.mep.axis.service.FeeEstimator;
import com.amrsamy.mep.axis.web.form.FeeEstimateForm;
import jakarta.validation.Valid;
import java.util.EnumSet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** The estimate has no side effects, so the result is rendered directly instead of redirecting. */
@Controller
@RequestMapping("/fee-estimator")
public class EstimatorController {

    private final FeeEstimator estimator;

    public EstimatorController(FeeEstimator estimator) {
        this.estimator = estimator;
    }

    @ModelAttribute("complexities")
    public Complexity[] complexities() {
        return Complexity.values();
    }

    @ModelAttribute("disciplineOptions")
    public Discipline[] disciplineOptions() {
        return Discipline.values();
    }

    @GetMapping
    public String form(Model model) {
        FeeEstimateForm form = new FeeEstimateForm();
        form.setComplexity(Complexity.STANDARD);
        model.addAttribute("estimateForm", form);
        return "estimator";
    }

    @PostMapping
    public String estimate(@Valid @ModelAttribute("estimateForm") FeeEstimateForm form, BindingResult result,
                           Model model) {
        if (!result.hasErrors()) {
            model.addAttribute("estimate", estimator.estimate(form.getArea(), form.getComplexity(),
                    EnumSet.copyOf(form.getDisciplines()), form.isBim(), form.isSupervision()));
        }
        return "estimator";
    }
}
