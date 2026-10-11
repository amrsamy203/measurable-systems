package com.amrsamy.mep.meridian.web;

import com.amrsamy.mep.meridian.service.ProjectCatalogService;
import com.amrsamy.mep.meridian.service.ServiceCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final ProjectCatalogService projects;
    private final ServiceCatalogService services;

    public PageController(ProjectCatalogService projects, ServiceCatalogService services) {
        this.projects = projects;
        this.services = services;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("services", services.all());
        model.addAttribute("featured", projects.featured());
        model.addAttribute("sectorCounts", projects.countsBySector());
        model.addAttribute("projectCount", projects.all().size());
        model.addAttribute("totalArea", projects.totalAreaSqm());
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("projectCount", projects.all().size());
        return "about";
    }
}
