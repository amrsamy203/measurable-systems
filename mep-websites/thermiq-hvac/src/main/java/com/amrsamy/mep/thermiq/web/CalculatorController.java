package com.amrsamy.mep.thermiq.web;

import com.amrsamy.mep.thermiq.service.CoolingLoadCalculator;
import com.amrsamy.mep.thermiq.web.form.CalculatorForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/calculator")
public class CalculatorController {

    private final CoolingLoadCalculator calculator;

    public CalculatorController(CoolingLoadCalculator calculator) {
        this.calculator = calculator;
    }

    @ModelAttribute("rooms")
    public CoolingLoadCalculator.RoomType[] rooms() {
        return CoolingLoadCalculator.RoomType.values();
    }

    @ModelAttribute("exposures")
    public CoolingLoadCalculator.SunExposure[] exposures() {
        return CoolingLoadCalculator.SunExposure.values();
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("calc", new CalculatorForm());
        return "calculator";
    }

    @PostMapping
    public String calculate(@Valid @ModelAttribute("calc") CalculatorForm form, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            model.addAttribute("result", calculator.recommend(form.getArea(), form.getRoom(), form.getSun(),
                    form.getOccupants()));
        }
        return "calculator";
    }
}
