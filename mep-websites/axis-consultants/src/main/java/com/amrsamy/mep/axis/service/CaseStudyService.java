package com.amrsamy.mep.axis.service;

import com.amrsamy.mep.axis.model.CaseStudy;
import com.amrsamy.mep.axis.model.Sector;
import com.amrsamy.mep.axis.model.Stage;
import com.amrsamy.mep.axis.repository.CaseStudyRepository;
import com.amrsamy.mep.axis.web.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CaseStudyService {

    private final CaseStudyRepository repository;

    public CaseStudyService(CaseStudyRepository repository) {
        this.repository = repository;
    }

    public List<CaseStudy> all() {
        return repository.findAll();
    }

    public List<CaseStudy> featured() {
        return repository.findAll().stream().filter(CaseStudy::featured).toList();
    }

    /** Both filters are optional and combine with AND. */
    public List<CaseStudy> filter(Optional<Sector> sector, Optional<Stage> stage) {
        return repository.findAll().stream()
                .filter(c -> sector.map(s -> c.sector() == s).orElse(true))
                .filter(c -> stage.map(c::covers).orElse(true))
                .toList();
    }

    public CaseStudy get(String slug) {
        return repository.findAll().stream()
                .filter(c -> c.slug().equals(slug))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No case study " + slug));
    }

    /** The case study after this one, wrapping around, for the "next project" link. */
    public CaseStudy next(CaseStudy current) {
        List<CaseStudy> all = repository.findAll();
        int index = all.indexOf(current);
        return all.get((index + 1) % all.size());
    }

    public long totalArea() {
        return repository.findAll().stream().mapToLong(CaseStudy::areaSqm).sum();
    }
}
