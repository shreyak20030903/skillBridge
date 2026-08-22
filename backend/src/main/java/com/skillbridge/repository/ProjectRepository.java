package com.skillbridge.repository;

import com.skillbridge.model.Project;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ProjectRepository extends Neo4jRepository<Project, String> {
}
