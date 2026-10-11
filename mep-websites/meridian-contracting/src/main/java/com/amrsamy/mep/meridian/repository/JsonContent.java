package com.amrsamy.mep.meridian.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import org.springframework.core.io.Resource;

/** Site copy lives in JSON under resources/content so it can be edited without touching Java. */
final class JsonContent {

    private JsonContent() {}

    static <T> List<T> readList(ObjectMapper mapper, Resource resource, TypeReference<List<T>> type) {
        try (InputStream in = resource.getInputStream()) {
            return List.copyOf(mapper.readValue(in, type));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read content from " + resource.getDescription(), e);
        }
    }
}
