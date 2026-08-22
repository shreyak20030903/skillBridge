package com.skillbridge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@Data
@NoArgsConstructor
public class HasSkillRelationship {

    @RelationshipId
    private Long id;

    /** Beginner, Intermediate, Advanced, Expert */
    private String level;

    private Integer yearsExperience;

    @TargetNode
    private Skill skill;

    public HasSkillRelationship(Skill skill, String level, Integer yearsExperience) {
        this.skill = skill;
        this.level = level;
        this.yearsExperience = yearsExperience;
    }
}
