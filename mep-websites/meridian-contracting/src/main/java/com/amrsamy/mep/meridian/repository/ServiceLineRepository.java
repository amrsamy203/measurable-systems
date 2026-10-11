package com.amrsamy.mep.meridian.repository;

import com.amrsamy.mep.meridian.model.ServiceLine;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceLineRepository {

    private final List<ServiceLine> lines;

    public ServiceLineRepository(ObjectMapper mapper, @Value("classpath:content/services.json") Resource source) {
        this.lines = JsonContent.readList(mapper, source, new TypeReference<List<ServiceLine>>() {});
    }

    public List<ServiceLine> findAll() {
        return lines;
    }

    public Optional<ServiceLine> findBySlug(String slug) {
        return lines.stream().filter(s -> s.slug().equals(slug)).findFirst();
    }
}
