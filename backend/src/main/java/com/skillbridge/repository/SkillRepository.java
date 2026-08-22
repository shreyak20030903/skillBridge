package com.skillbridge.repository;

import com.skillbridge.model.Skill;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends Neo4jRepository<Skill, String> {
    Optional<Skill> findByNameIgnoreCase(String name);
    List<Skill> findByNameContainingIgnoreCase(String partial);
}
