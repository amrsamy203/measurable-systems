package com.amrsamy.mep.axis.repository;

import com.amrsamy.mep.axis.model.DisciplineProfile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class DisciplineRepository {

    private final List<DisciplineProfile> profiles;

    DisciplineRepository(ObjectMapper mapper, @Value("classpath:content/disciplines.json") Resource source) {
        this.profiles = JsonContent.readList(mapper, source, new TypeReference<List<DisciplineProfile>>() {}).stream()
                .sorted(Comparator.comparing(DisciplineProfile::discipline))
                .toList();
    }

    public List<DisciplineProfile> findAll() {
        return profiles;
    }
}
