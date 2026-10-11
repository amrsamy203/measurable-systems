package com.amrsamy.mep.meridian.service;

import com.amrsamy.mep.meridian.model.Project;
import com.amrsamy.mep.meridian.model.Sector;
import com.amrsamy.mep.meridian.repository.ProjectRepository;
import com.amrsamy.mep.meridian.web.NotFoundException;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ProjectCatalogService {

    private final ProjectRepository repository;

    public ProjectCatalogService(ProjectRepository repository) {
        this.repository = repository;
    }

    public List<Project> all() {
        return repository.findAll();
    }

    public List<Project> featured() {
        return repository.findAll().stream().filter(Project::featured).toList();
    }

    public List<Project> inSector(Optional<Sector> sector) {
        return sector.map(s -> repository.findAll().stream().filter(p -> p.sector() == s).toList())
                .orElseGet(repository::findAll);
    }

    public List<Project> withDiscipline(String discipline) {
        return repository.findAll().stream().filter(p -> p.covers(discipline)).toList();
    }

    public Project get(String slug) {
        return repository.findBySlug(slug).orElseThrow(() -> new NotFoundException("No project " + slug));
    }

    /** Same sector first, then the projects sharing the most disciplines. */
    public List<Project> related(Project project, int limit) {
        Comparator<Project> bySimilarity = Comparator
                .comparing((Project p) -> p.sector() != project.sector())
                .thenComparing(p -> -sharedDisciplines(project, p));
        return repository.findAll().stream()
                .filter(p -> !p.slug().equals(project.slug()))
                .sorted(bySimilarity)
                .limit(limit)
                .toList();
    }

    /** Every sector in display order, including empty ones, so filter chips stay stable. */
    public Map<Sector, Long> countsBySector() {
        Map<Sector, Long> counts = new EnumMap<>(Sector.class);
        for (Sector sector : Sector.values()) {
            counts.put(sector, 0L);
        }
        repository.findAll().forEach(p -> counts.merge(p.sector(), 1L, Long::sum));
        return counts;
    }

    public long totalAreaSqm() {
        return repository.findAll().stream().mapToLong(Project::areaSqm).sum();
    }

    private static long sharedDisciplines(Project a, Project b) {
        return b.disciplines().stream().filter(a::covers).count();
    }
}
