package com.amrsamy.mep.axis.web;

import com.amrsamy.mep.axis.model.CaseStudy;
import com.amrsamy.mep.axis.model.Sector;
import com.amrsamy.mep.axis.model.Stage;
import com.amrsamy.mep.axis.service.CaseStudyService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/work")
public class WorkController {

    private final CaseStudyService cases;

    public WorkController(CaseStudyService cases) {
        this.cases = cases;
    }

    /** Unknown filter values are ignored rather than rejected, so stale links still show work. */
    @GetMapping
    public String list(@RequestParam(required = false) String sector,
                       @RequestParam(required = false) String stage,
                       Model model) {
        Optional<Sector> selectedSector = Sector.fromSlug(sector);
        Optional<Stage> selectedStage = Stage.fromSlug(stage);
        model.addAttribute("cases", cases.filter(selectedSector, selectedStage));
        model.addAttribute("sectors", Sector.values());
        model.addAttribute("stages", Stage.values());
        model.addAttribute("selectedSector", selectedSector.orElse(null));
        model.addAttribute("selectedStage", selectedStage.orElse(null));
        model.addAttribute("total", cases.all().size());
        return "work/list";
    }

    @GetMapping("/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        CaseStudy study = cases.get(slug);
        model.addAttribute("study", study);
        model.addAttribute("next", cases.next(study));
        model.addAttribute("stages", Stage.values());
        return "work/detail";
    }
}
