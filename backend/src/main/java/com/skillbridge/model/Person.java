package com.skillbridge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Person")
@Data
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue
    private String id;

    private String name;
    private String title;
    private String bio;
    private String location;
    private Boolean mentorAvailable;
    private Integer yearsExperience;

    @Relationship(type = "HAS_SKILL", direction = Relationship.Direction.OUTGOING)
    private List<HasSkillRelationship> skills = new ArrayList<>();

    @Relationship(type = "WORKED_ON", direction = Relationship.Direction.OUTGOING)
    private List<WorkedOnRelationship> projects = new ArrayList<>();

    @Relationship(type = "WORKED_AT", direction = Relationship.Direction.OUTGOING)
    private List<WorkedAtRelationship> companies = new ArrayList<>();

    /** Endorsements this person has RECEIVED (edge points from endorser to this person). */
    @Relationship(type = "ENDORSED", direction = Relationship.Direction.INCOMING)
    private List<EndorsedRelationship> endorsementsReceived = new ArrayList<>();
}
