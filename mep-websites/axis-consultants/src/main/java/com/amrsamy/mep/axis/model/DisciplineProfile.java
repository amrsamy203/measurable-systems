package com.amrsamy.mep.axis.model;

import java.util.List;

public record DisciplineProfile(
        Discipline discipline,
        String summary,
        List<String> scope,
        List<String> tools,
        List<String> standards) {}
