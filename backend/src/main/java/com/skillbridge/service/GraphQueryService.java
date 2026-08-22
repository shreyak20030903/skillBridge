package com.skillbridge.service;

import com.skillbridge.dto.ConnectionPathDto;
import com.skillbridge.dto.LeaderboardEntryDto;
import com.skillbridge.dto.MentorSuggestionDto;
import com.skillbridge.dto.PersonSummaryDto;
import com.skillbridge.dto.ProjectContributorDto;
import com.skillbridge.dto.ProjectSummaryDto;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * All queries here go through the official Neo4j Java driver directly (rather than
 * Spring Data Neo4j's repository abstraction) because they are the two queries the
 * assignment specifically calls out:
 *   1. findMentorsViaSharedProject — a 2+ hop traversal
 *   2. findShortestConnection      - a variable-length shortest path, which is
 *      awkward to express in SQL (would need a recursive CTE with no fixed depth).
 * Every query below is parameterised — no string concatenation of user input into Cypher.
 */
@Service
public class GraphQueryService {

    private final Driver driver;

    /**
     * Whitelisted relationship-type filters for the connection explorer.
     * Cypher does not allow parameterising relationship types inside a pattern,
     * so this is resolved server-side against a fixed set of known-safe values
     * rather than ever inserting raw user text into the query.
     */
    private static final Map<String, String> VIA_FILTERS = Map.of(
            "any", "",
            "work", ":WORKED_ON|WORKED_AT",
            "skills", ":HAS_SKILL",
            "endorsements", ":ENDORSED"
    );

    public GraphQueryService(Driver driver) {
        this.driver = driver;
    }

    /**
     * 2-hop traversal: find people who have the target skill at Advanced/Expert level
     * (or are marked as available mentors), and who are connected to the requester
     * through a shared project. This is the "multi-hop" required query.
     */
    public List<MentorSuggestionDto> findMentorsViaSharedProject(String requesterId, String targetSkillName) {
        String cypher = """
            MATCH (requester:Person {id: $requesterId})-[:WORKED_ON]->(proj:Project)<-[:WORKED_ON]-(mentor:Person)
            WHERE mentor.id <> $requesterId
            MATCH (mentor)-[hs:HAS_SKILL]->(s:Skill)
            WHERE toLower(s.name) = toLower($skillName)
              AND (mentor.mentorAvailable = true OR hs.level IN ['Advanced', 'Expert'])
            OPTIONAL MATCH (mentor)<-[e:ENDORSED {skillName: s.name}]-(:Person)
            WITH mentor, hs.level AS level, proj, count(e) AS endorsementCount
            RETURN DISTINCT mentor, level, proj.name AS projectName, endorsementCount
            ORDER BY endorsementCount DESC
            LIMIT 20
            """;

        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher, Map.of(
                        "requesterId", requesterId,
                        "skillName", targetSkillName));
                List<MentorSuggestionDto> mentors = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Node mentorNode = record.get("mentor").asNode();
                    PersonSummaryDto summary = toPersonSummary(mentorNode);
                    mentors.add(new MentorSuggestionDto(
                            summary,
                            record.get("level").asString(null),
                            record.get("projectName").asString(null),
                            record.get("endorsementCount").asLong(0)));
                }
                return mentors;
            });
        }
    }

    /**
     * Shortest path between two people, optionally restricted to a relationship-type
     * category ("work", "skills", "endorsements") — otherwise "any" relationship type.
     * A fixed-depth-free shortest path like this needs a recursive CTE in SQL;
     * in Cypher it is a single pattern.
     */
    public ConnectionPathDto findShortestConnection(String fromPersonId, String toPersonId, String via) {
        String relFilter = VIA_FILTERS.getOrDefault(via, VIA_FILTERS.get("any"));
        String cypher = """
            MATCH (a:Person {id: $fromId}), (b:Person {id: $toId})
            MATCH path = shortestPath((a)-[%s*..6]-(b))
            RETURN path
            """.formatted(relFilter);

        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher, Map.of("fromId", fromPersonId, "toId", toPersonId));
                if (!result.hasNext()) {
                    return null;
                }
                Path path = result.next().get("path").asPath();

                List<String> labels = new ArrayList<>();
                List<String> names = new ArrayList<>();
                List<String> relTypes = new ArrayList<>();

                for (Node node : path.nodes()) {
                    labels.add(node.labels().iterator().next());
                    names.add(firstNonNull(node, "name"));
                }
                for (Relationship rel : path.relationships()) {
                    relTypes.add(rel.type());
                }

                return new ConnectionPathDto(labels, names, relTypes, path.length());
            });
        }
    }

    /** All projects, each with how many people worked on it. */
    public List<ProjectSummaryDto> findProjectsWithContributorCounts() {
        String cypher = """
            MATCH (pr:Project)
            OPTIONAL MATCH (pr)<-[:WORKED_ON]-(p:Person)
            RETURN pr, count(p) AS contributorCount
            ORDER BY contributorCount DESC, pr.name
            """;
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher);
                List<ProjectSummaryDto> projects = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Node pr = record.get("pr").asNode();
                    projects.add(new ProjectSummaryDto(
                            pr.get("id").asString(null),
                            pr.get("name").asString(null),
                            pr.get("description").asString(null),
                            record.get("contributorCount").asLong(0)));
                }
                return projects;
            });
        }
    }

    /**
     * Everyone who worked on a given project — this is the "people who worked
     * on similar projects" discovery feature: anyone browsing a project sees
     * everyone else connected to it.
     */
    public List<ProjectContributorDto> findContributorsForProject(String projectId) {
        String cypher = """
            MATCH (p:Person)-[w:WORKED_ON]->(pr:Project {id: $projectId})
            RETURN p, w.role AS role
            ORDER BY p.name
            """;
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher, Map.of("projectId", projectId));
                List<ProjectContributorDto> contributors = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    contributors.add(new ProjectContributorDto(
                            toPersonSummary(record.get("p").asNode()),
                            record.get("role").asString(null)));
                }
                return contributors;
            });
        }
    }

    /** Top people by total endorsements received, across all skills. */
    public List<LeaderboardEntryDto> findEndorsementLeaderboard() {
        String cypher = """
            MATCH (p:Person)<-[e:ENDORSED]-(:Person)
            RETURN p, count(e) AS total
            ORDER BY total DESC
            LIMIT 10
            """;
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                var result = tx.run(cypher);
                List<LeaderboardEntryDto> entries = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    entries.add(new LeaderboardEntryDto(
                            toPersonSummary(record.get("p").asNode()),
                            record.get("total").asLong(0)));
                }
                return entries;
            });
        }
    }

    private PersonSummaryDto toPersonSummary(Node node) {
        return new PersonSummaryDto(
                node.get("id").asString(null),
                node.get("name").asString(null),
                node.get("title").asString(null),
                node.get("location").asString(null));
    }

    private String firstNonNull(Node node, String property) {
        return node.containsKey(property) ? node.get(property).asString(null) : null;
    }
}