package com.amrsamy.mep.meridian.repository;

import com.amrsamy.mep.meridian.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    /** Newest first. */
    List<Project> findAll();

    Optional<Project> findBySlug(String slug);
}
