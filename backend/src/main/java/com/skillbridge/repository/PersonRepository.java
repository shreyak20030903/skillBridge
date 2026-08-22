package com.skillbridge.repository;

import com.skillbridge.model.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonRepository extends Neo4jRepository<Person, String> {

    List<Person> findByNameContainingIgnoreCase(String name);

    /**
     * Single-hop: everyone who has a given skill, sorted by their level and
     * how many endorsements they've received for that skill.
     */
    @Query("""
        MATCH (p:Person)-[hs:HAS_SKILL]->(s:Skill)
        WHERE toLower(s.name) = toLower($skillName)
        OPTIONAL MATCH (p)<-[e:ENDORSED {skillName: s.name}]-(:Person)
        WITH p, hs.yearsExperience AS years, count(e) AS endorsementCount
        RETURN p
        ORDER BY endorsementCount DESC, years DESC
        """)
    List<Person> findPeopleWithSkill(@Param("skillName") String skillName);
}