package com.skillbridge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryDto {
    private String id;
    private String name;
    private String description;
    private long contributorCount;
}