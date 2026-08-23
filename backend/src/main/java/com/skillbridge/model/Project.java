package com.skillbridge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Project")
@Data
@NoArgsConstructor
public class Project {

    @Id
    private String id;

    private String name;
    private String description;
    private String startDate;
    private String endDate;
}
