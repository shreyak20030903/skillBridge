package com.skillbridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionPathDto {
    /** Alternating node labels: Person, Skill/Project/Company, Person, ... */
    private List<String> nodeLabels;
    /** Display names for each node in the path, same order as nodeLabels. */
    private List<String> nodeNames;
    /** Relationship type names connecting each pair of nodes above. */
    private List<String> relationshipTypes;
    private int hops;
}
