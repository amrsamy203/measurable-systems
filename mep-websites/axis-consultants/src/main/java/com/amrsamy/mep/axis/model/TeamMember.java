package com.amrsamy.mep.axis.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public record TeamMember(String name, String role, String credentials, String bio) {

    public String initials() {
        return Arrays.stream(name.split("\\s+"))
                .filter(part -> !part.isBlank())
                .limit(2)
                .map(part -> part.substring(0, 1))
                .collect(Collectors.joining());
    }
}
