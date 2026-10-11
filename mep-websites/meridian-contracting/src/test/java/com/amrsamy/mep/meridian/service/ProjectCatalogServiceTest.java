package com.amrsamy.mep.meridian.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amrsamy.mep.meridian.model.Project;
import com.amrsamy.mep.meridian.model.Sector;
import com.amrsamy.mep.meridian.repository.ProjectRepository;
import com.amrsamy.mep.meridian.web.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ProjectCatalogServiceTest {

    private static Project project(String slug, Sector sector, int year, boolean featured, String... disciplines) {
        return new Project(slug, slug, sector, "Cairo", year, Project.Status.COMPLETED, 10_000, "Client",
                List.of(disciplines), "x.jpg", featured, "summary", "challenge", List.of(), List.of(), List.of());
    }

    private final List<Project> data = List.of(
            project("hospital-a", Sector.HEALTHCARE, 2024, true, "hvac", "electrical", "plumbing"),
            project("hospital-b", Sector.HEALTHCARE, 2022, false, "fire-protection"),
            project("tower", Sector.COMMERCIAL, 2025, true, "hvac", "electrical", "plumbing", "bms"),
            project("school", Sector.EDUCATION, 2023, false, "hvac"));

    private final ProjectCatalogService catalog = new ProjectCatalogService(new ProjectRepository() {
        @Override
        public List<Project> findAll() {
            return data;
        }

        @Override
        public Optional<Project> findBySlug(String slug) {
            return data.stream().filter(p -> p.slug().equals(slug)).findFirst();
        }
    });

    @Test
    void filtersBySectorAndFallsBackToAllWithoutOne() {
        assertThat(catalog.inSector(Optional.of(Sector.HEALTHCARE))).extracting(Project::slug)
                .containsExactly("hospital-a", "hospital-b");
        assertThat(catalog.inSector(Optional.empty())).hasSize(4);
    }

    @Test
    void relatedPrefersSameSectorThenSharedDisciplines() {
        List<Project> related = catalog.related(catalog.get("hospital-a"), 3);

        assertThat(related).extracting(Project::slug).containsExactly("hospital-b", "tower", "school");
    }

    @Test
    void countsIncludeEmptySectorsInDisplayOrder() {
        var counts = catalog.countsBySector();

        assertThat(counts.keySet()).containsExactly(Sector.values());
        assertThat(counts.get(Sector.HEALTHCARE)).isEqualTo(2);
        assertThat(counts.get(Sector.DATA_CENTRES)).isZero();
    }

    @Test
    void featuredAndDisciplineQueries() {
        assertThat(catalog.featured()).extracting(Project::slug).containsExactly("hospital-a", "tower");
        assertThat(catalog.withDiscipline("bms")).extracting(Project::slug).containsExactly("tower");
        assertThat(catalog.totalAreaSqm()).isEqualTo(40_000);
    }

    @Test
    void unknownSlugIsNotFound() {
        assertThatThrownBy(() -> catalog.get("missing")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void sectorSlugsRoundTrip() {
        assertThat(Sector.fromSlug("data-centres")).contains(Sector.DATA_CENTRES);
        assertThat(Sector.fromSlug("unknown")).isEmpty();
        assertThat(Sector.fromSlug(null)).isEmpty();
    }
}
