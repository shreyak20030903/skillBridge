package com.skillbridge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

/**
 * Represents an incoming (:Person)-[:ENDORSED]->(:Person) edge.
 * The target node here is the ENDORSER (the person who gave the endorsement),
 * since this relationship is mapped with INCOMING direction on Person.endorsementsReceived.
 */
@RelationshipProperties
@Data
@NoArgsConstructor
public class EndorsedRelationship {

    @RelationshipId
    private Long id;

    private String skillName;
    private Integer weight;
    private String note;

    @TargetNode
    private Person endorser;

    public EndorsedRelationship(Person endorser, String skillName, Integer weight, String note) {
        this.endorser = endorser;
        this.skillName = skillName;
        this.weight = weight;
        this.note = note;
    }
}
