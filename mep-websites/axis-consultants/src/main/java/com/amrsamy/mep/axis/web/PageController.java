package com.amrsamy.mep.axis.web;

import com.amrsamy.mep.axis.model.Discipline;
import com.amrsamy.mep.axis.model.Stage;
import com.amrsamy.mep.axis.repository.DisciplineRepository;
import com.amrsamy.mep.axis.repository.TeamRepository;
import com.amrsamy.mep.axis.service.CaseStudyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final CaseStudyService cases;
    private final DisciplineRepository disciplines;
    private final TeamRepository team;

    public PageController(CaseStudyService cases, DisciplineRepository disciplines, TeamRepository team) {
        this.cases = cases;
        this.disciplines = disciplines;
        this.team = team;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", cases.featured());
        model.addAttribute("disciplines", Discipline.values());
        model.addAttribute("stages", Stage.values());
        model.addAttribute("caseCount", cases.all().size());
        model.addAttribute("totalArea", cases.totalArea());
        return "home";
    }

    @GetMapping("/expertise")
    public String expertise(Model model) {
        model.addAttribute("profiles", disciplines.findAll());
        model.addAttribute("stages", Stage.values());
        return "expertise";
    }

    @GetMapping("/studio")
    public String studio(Model model) {
        model.addAttribute("team", team.findAll());
        return "studio";
    }
}
