package com.skillbridge.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Company")
@Data
@NoArgsConstructor
public class Company {

    @Id
    private String id;

    private String name;
    private String industry;
}
