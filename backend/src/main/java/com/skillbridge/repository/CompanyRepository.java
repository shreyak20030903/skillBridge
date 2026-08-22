package com.skillbridge.repository;

import com.skillbridge.model.Company;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CompanyRepository extends Neo4jRepository<Company, String> {
}
