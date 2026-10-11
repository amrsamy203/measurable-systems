package com.amrsamy.mep.meridian.service;

import com.amrsamy.mep.meridian.model.ServiceLine;
import com.amrsamy.mep.meridian.repository.ServiceLineRepository;
import com.amrsamy.mep.meridian.web.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ServiceCatalogService {

    private final ServiceLineRepository repository;

    public ServiceCatalogService(ServiceLineRepository repository) {
        this.repository = repository;
    }

    public List<ServiceLine> all() {
        return repository.findAll();
    }

    public ServiceLine get(String slug) {
        return repository.findBySlug(slug).orElseThrow(() -> new NotFoundException("No service " + slug));
    }

    /** Slug → service, for templates that render a project's discipline list. */
    public Map<String, ServiceLine> bySlug() {
        return repository.findAll().stream().collect(Collectors.toMap(ServiceLine::slug, Function.identity()));
    }

    public List<String> namesFor(List<String> slugs) {
        Map<String, ServiceLine> lookup = bySlug();
        return slugs.stream().filter(lookup::containsKey).map(s -> lookup.get(s).name()).toList();
    }
}
