package com.amrsamy.mep.axis.repository;

import com.amrsamy.mep.axis.model.CaseStudy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

@Repository
class JsonCaseStudyRepository implements CaseStudyRepository {

    private final List<CaseStudy> cases;

    JsonCaseStudyRepository(ObjectMapper mapper, @Value("classpath:content/work.json") Resource source) {
        this.cases = JsonContent.readList(mapper, source, new TypeReference<List<CaseStudy>>() {}).stream()
                .sorted(Comparator.comparingInt(CaseStudy::year).reversed())
                .toList();
    }

    @Override
    public List<CaseStudy> findAll() {
        return cases;
    }
}
