package com.skillbridge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@NoArgsConstructor
public class WorkedAtRelationship {

    @RelationshipId
    private Long id;

    private String role;
    private String startDate;
    private String endDate;

    @TargetNode
    private Company company;

    public WorkedAtRelationship(Company company, String role, String startDate, String endDate) {
        this.company = company;
        this.role = role;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
