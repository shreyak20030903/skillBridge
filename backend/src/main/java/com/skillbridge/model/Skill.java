package com.skillbridge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Skill")
@Data
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue
    private String id;

    private String name;

    /** e.g. "Backend", "Frontend", "DevOps", "Data" */
    private String category;

    public Skill(String name, String category) {
        this.name = name;
        this.category = category;
    }
}
