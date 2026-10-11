package com.amrsamy.mep.meridian.web;

import com.amrsamy.mep.meridian.model.Project;
import com.amrsamy.mep.meridian.model.Sector;
import com.amrsamy.mep.meridian.service.ProjectCatalogService;
import com.amrsamy.mep.meridian.service.ServiceCatalogService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/projects")
public class ProjectsController {

    private final ProjectCatalogService projects;
    private final ServiceCatalogService services;

    public ProjectsController(ProjectCatalogService projects, ServiceCatalogService services) {
        this.projects = projects;
        this.services = services;
    }

    /** Filtering is server-side, so every filtered view has its own shareable URL. */
    @GetMapping
    public String list(@RequestParam(required = false) String sector, Model model) {
        Optional<Sector> selected = Sector.fromSlug(sector);
        model.addAttribute("projects", projects.inSector(selected));
        model.addAttribute("selectedSector", selected.orElse(null));
        model.addAttribute("sectorCounts", projects.countsBySector());
        model.addAttribute("total", projects.all().size());
        return "projects/list";
    }

    @GetMapping("/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        Project project = projects.get(slug);
        model.addAttribute("project", project);
        model.addAttribute("disciplineNames", services.namesFor(project.disciplines()));
        model.addAttribute("related", projects.related(project, 3));
        return "projects/detail";
    }
}
