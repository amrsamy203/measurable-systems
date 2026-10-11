package com.amrsamy.mep.axis.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amrsamy.mep.axis.model.CaseStudy;
import com.amrsamy.mep.axis.model.Discipline;
import com.amrsamy.mep.axis.model.Sector;
import com.amrsamy.mep.axis.model.Stage;
import com.amrsamy.mep.axis.web.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CaseStudyServiceTest {

    private final CaseStudy hospital = study("hospital", Sector.HEALTHCARE, true, Stage.DETAILED, Stage.SUPERVISION);
    private final CaseStudy clinic = study("clinic", Sector.HEALTHCARE, false, Stage.CONCEPT);
    private final CaseStudy office = study("office", Sector.WORKPLACE, true, Stage.CONCEPT, Stage.DETAILED);

    private final CaseStudyService service = new CaseStudyService(() -> List.of(hospital, clinic, office));

    @Test
    void filtersCombineSectorAndStage() {
        assertThat(service.filter(Optional.of(Sector.HEALTHCARE), Optional.empty())).containsExactly(hospital, clinic);
        assertThat(service.filter(Optional.empty(), Optional.of(Stage.DETAILED))).containsExactly(hospital, office);
        assertThat(service.filter(Optional.of(Sector.HEALTHCARE), Optional.of(Stage.CONCEPT))).containsExactly(clinic);
        assertThat(service.filter(Optional.of(Sector.HOSPITALITY), Optional.empty())).isEmpty();
        assertThat(service.filter(Optional.empty(), Optional.empty())).hasSize(3);
    }

    @Test
    void nextWrapsAroundToTheFirstCase() {
        assertThat(service.next(hospital)).isEqualTo(clinic);
        assertThat(service.next(office)).isEqualTo(hospital);
    }

    @Test
    void featuredAndTotals() {
        assertThat(service.featured()).containsExactly(hospital, office);
        assertThat(service.totalArea()).isEqualTo(3_000);
    }

    @Test
    void unknownSlugIsNotFound() {
        assertThat(service.get("office")).isEqualTo(office);
        assertThatThrownBy(() -> service.get("nope")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void stageAndSectorSlugsRoundTrip() {
        assertThat(Stage.fromSlug("supervision")).contains(Stage.SUPERVISION);
        assertThat(Sector.fromSlug("mission-critical")).contains(Sector.MISSION_CRITICAL);
        assertThat(Sector.fromSlug(null)).isEmpty();
    }

    private static CaseStudy study(String slug, Sector sector, boolean featured, Stage... stages) {
        return new CaseStudy(slug, slug, "client", "Cairo", 2025, sector, List.of(stages),
                List.of(Discipline.MECHANICAL), 1_000, null, null, featured, "headline", "brief",
                List.of(), List.of(), List.of());
    }
}
