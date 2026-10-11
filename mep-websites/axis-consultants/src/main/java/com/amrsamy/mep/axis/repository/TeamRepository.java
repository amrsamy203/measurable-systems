package com.amrsamy.mep.axis.repository;

import com.amrsamy.mep.axis.model.TeamMember;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepository {

    private final List<TeamMember> team;

    TeamRepository(ObjectMapper mapper, @Value("classpath:content/team.json") Resource source) {
        this.team = JsonContent.readList(mapper, source, new TypeReference<List<TeamMember>>() {});
    }

    public List<TeamMember> findAll() {
        return team;
    }
}
