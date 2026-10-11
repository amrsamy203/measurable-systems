package com.amrsamy.mep.meridian.web;

import com.amrsamy.mep.meridian.model.ServiceLine;
import com.amrsamy.mep.meridian.service.ProjectCatalogService;
import com.amrsamy.mep.meridian.service.ServiceCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/services")
public class ServicesController {

    private final ServiceCatalogService services;
    private final ProjectCatalogService projects;

    public ServicesController(ServiceCatalogService services, ProjectCatalogService projects) {
        this.services = services;
        this.projects = projects;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("services", services.all());
        return "services/list";
    }

    @GetMapping("/{slug}")
    public String detail(@PathVariable String slug, Model model) {
        ServiceLine service = services.get(slug);
        model.addAttribute("service", service);
        model.addAttribute("projects", projects.withDiscipline(slug).stream().limit(3).toList());
        model.addAttribute("otherServices", services.all().stream().filter(s -> !s.slug().equals(slug)).toList());
        return "services/detail";
    }
}
