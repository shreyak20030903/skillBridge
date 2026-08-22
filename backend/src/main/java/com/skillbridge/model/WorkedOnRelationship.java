package com.skillbridge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@NoArgsConstructor
public class WorkedOnRelationship {

    @RelationshipId
    private Long id;

    private String role;

    @TargetNode
    private Project project;

    public WorkedOnRelationship(Project project, String role) {
        this.project = project;
        this.role = role;
    }
}
