package com.amrsamy.mep.meridian.repository;

import com.amrsamy.mep.meridian.model.Project;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class JsonProjectRepository implements ProjectRepository {

    private final List<Project> projects;

    public JsonProjectRepository(ObjectMapper mapper, @Value("classpath:content/projects.json") Resource source) {
        this.projects = JsonContent.readList(mapper, source, new TypeReference<List<Project>>() {}).stream()
                .sorted(Comparator.comparingInt(Project::year).reversed())
                .toList();
    }

    @Override
    public List<Project> findAll() {
        return projects;
    }

    @Override
    public Optional<Project> findBySlug(String slug) {
        return projects.stream().filter(p -> p.slug().equals(slug)).findFirst();
    }
}
